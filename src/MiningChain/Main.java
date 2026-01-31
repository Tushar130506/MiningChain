package MiningChain;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class Main extends JavaPlugin implements Listener {

    private Map<UUID, Integer> balances = new HashMap<>();
    private Map<UUID, String> wallets = new HashMap<>();
    private List<String> blockchain = new ArrayList<>();

    private int blockIndex = 0;
    private int difficulty = 3;
    private boolean debug = false;

    private File chainFile;
    private File balanceFile;
    private File walletFile;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        chainFile = new File(getDataFolder(), "chain.txt");
        balanceFile = new File(getDataFolder(), "balances.txt");
        walletFile = new File(getDataFolder(), "wallets.txt");

        loadBalances();
        loadWallets();

        getLogger().info("[CORE] MiningChain FINAL enabled!");
    }

    // ===================== BLOCK MINING =====================

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Material m = e.getBlock().getType();

        int reward = getReward(m);
        if (reward == 0) return;

        UUID id = p.getUniqueId();
        balances.put(id, balances.getOrDefault(id, 0) + reward);

        String prevHash = blockchain.isEmpty() ? "0" : blockchain.get(blockchain.size() - 1).split("\\|")[3];
        String data = p.getName() + ":" + m + ":" + reward;
        String hash = mineHash(blockIndex + data + prevHash);

        String block = "BLOCK#" + blockIndex +
                "|DATA=" + data +
                "|PREV=" + prevHash +
                "|HASH=" + hash;

        blockchain.add(block);
        append(chainFile, block);
        saveBalances();

        p.sendMessage("⛏ Mined " + m + " → +" + reward + " coins");

        if (debug) {
            getLogger().info("[CORE] Block mined by " + p.getName());
            getLogger().info("[CHAIN] Hash=" + hash);
        }

        blockIndex++;
        if (blockIndex % 10 == 0) difficulty++;
    }

    private int getReward(Material m) {
        switch (m) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                return 1;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                return 3;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                return 5;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                return 10;
            default:
                return 0;
        }
    }

    // ===================== PROOF OF WORK =====================

    private String mineHash(String input) {
        String target = "0".repeat(difficulty);
        int nonce = 0;
        while (true) {
            String hash = sha256(input + nonce);
            if (hash.startsWith(target)) return hash;
            nonce++;
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            return "ERR";
        }
    }

    // ===================== NFT PICKAXE =====================

    public ItemStack createNFTPickaxe(Player p) {
        ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = pick.getItemMeta();
        meta.setDisplayName("§bNFT Pickaxe");
        meta.setLore(Arrays.asList(
                "Owner: " + p.getName(),
                "Unique Asset",
                "Token: " + UUID.randomUUID()
        ));
        pick.setItemMeta(meta);
        return pick;
    }

    // ===================== LEADERBOARD GUI =====================

    public void openLeaderboard(Player p) {
        Inventory inv = Bukkit.createInventory(
                null, 9,
                "🏆 Top Miners | Blocks=" + blockIndex + " Diff=" + difficulty
        );

        balances.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(e -> {
                    OfflinePlayer op = Bukkit.getOfflinePlayer(e.getKey());
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    ItemMeta meta = head.getItemMeta();
                    meta.setDisplayName("§e" + op.getName());
                    meta.setLore(Arrays.asList("Coins: " + e.getValue()));
                    head.setItemMeta(meta);
                    inv.addItem(head);
                });

        if (debug) getLogger().info("[GUI] Leaderboard opened by " + p.getName());
        p.openInventory(inv);
    }

    // ===================== STORAGE =====================

    private void append(File f, String s) {
        try (FileWriter fw = new FileWriter(f, true)) {
            fw.write(s + "\n");
        } catch (IOException e) { }
    }

    private void saveBalances() {
        try (PrintWriter pw = new PrintWriter(balanceFile)) {
            for (UUID id : balances.keySet())
                pw.println(id + ":" + balances.get(id));
        } catch (IOException e) { }
    }

    private void loadBalances() {
        if (!balanceFile.exists()) return;
        try (Scanner sc = new Scanner(balanceFile)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(":");
                balances.put(UUID.fromString(p[0]), Integer.parseInt(p[1]));
            }
        } catch (Exception e) { }
    }

    private void saveWallets() {
        try (PrintWriter pw = new PrintWriter(walletFile)) {
            for (UUID id : wallets.keySet())
                pw.println(id + ":" + wallets.get(id));
        } catch (IOException e) { }
    }

    private void loadWallets() {
        if (!walletFile.exists()) return;
        try (Scanner sc = new Scanner(walletFile)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(":");
                wallets.put(UUID.fromString(p[0]), p[1]);
            }
        } catch (Exception e) { }
    }

    // ===================== BLOCKCHAIN DEBUG =====================

    public boolean verifyChain() {
        for (int i = 1; i < blockchain.size(); i++) {
            String prev = blockchain.get(i - 1).split("\\|")[3];
            String curPrev = blockchain.get(i).split("\\|")[2].replace("PREV=", "");
            if (!prev.equals(curPrev)) return false;
        }
        return true;
    }

    // ===================== COMMANDS =====================

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("wallet")) {
            wallets.putIfAbsent(p.getUniqueId(), UUID.randomUUID().toString());
            saveWallets();
            p.sendMessage("🔑 Wallet: " + wallets.get(p.getUniqueId()));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("balance")) {
            p.sendMessage("💰 Balance: " + balances.getOrDefault(p.getUniqueId(), 0));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("pay")) {
            if (args.length != 2) return true;
            Player t = Bukkit.getPlayer(args[0]);
            int amt = Integer.parseInt(args[1]);
            if (t == null) return true;

            int bal = balances.getOrDefault(p.getUniqueId(), 0);
            if (bal < amt) return true;

            balances.put(p.getUniqueId(), bal - amt);
            balances.put(t.getUniqueId(), balances.getOrDefault(t.getUniqueId(), 0) + amt);
            saveBalances();
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("nft")) {
            int price = 50;
            int bal = balances.getOrDefault(p.getUniqueId(), 0);

            if (bal < price) {
                p.sendMessage("❌ Not enough coins! NFT costs " + price);
                return true;
            }

            balances.put(p.getUniqueId(), bal - price);
            saveBalances();
            p.getInventory().addItem(createNFTPickaxe(p));
            p.sendMessage("🪓 NFT Pickaxe purchased!");

            if (debug) getLogger().info("[NFT] Minted for " + p.getName());
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("top")) {
            openLeaderboard(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("debug")) {
            debug = !debug;
            p.sendMessage("🛠 Debug mode: " + debug);
            getLogger().info("[CORE] Debug toggled to " + debug);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("inspectchain")) {
            if (blockchain.isEmpty()) {
                p.sendMessage("⛓ Blockchain empty");
                return true;
            }
            String last = blockchain.get(blockchain.size() - 1);
            p.sendMessage("📦 Last Block:");
            p.sendMessage(last.replace("|", "\n"));
            if (debug) getLogger().info("[CHAIN] Inspect: " + last);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("verifychain")) {
            boolean ok = verifyChain();
            p.sendMessage(ok ? "✅ Chain valid" : "❌ Chain corrupted");
            getLogger().info("[CHAIN] Verification=" + ok);
            return true;
        }

        return false;
    }
}

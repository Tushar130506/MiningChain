# 🪙 MiningChain

**Minecraft Blockchain Mining Simulator**

> An interactive blockchain simulation where every mined ore becomes a cryptographically linked transaction.

---

## 📌 Problem Statement

Core blockchain mechanisms such as proof-of-work, block linking, and reward distribution are conceptually complex and difficult to demonstrate in an intuitive way. Existing blockchain platforms expose these processes through command-line tools, APIs, or financial transactions, which require technical knowledge and external infrastructure to understand or experiment with.  
As a result, blockchain education and prototyping remain abstract, limiting hands-on exploration and making it harder to observe how blocks are created, linked, and validated in real time.

There is a need for an interactive system that allows users to experience blockchain behavior directly through observable actions, enabling real-time experimentation with mining, rewards, and chain integrity in an accessible environment.

---

## 💡 Solution

MiningChain transforms Minecraft mining into a blockchain simulation:

- Each ore mined creates a blockchain block  
- Blocks are cryptographically linked using hashes  
- Players receive token rewards  
- NFT-style items can be minted  
- A GUI leaderboard visualizes the economy  
- Debugging tools allow inspection and verification of the chain  

---

## 🧱 System Architecture

```
Player mines block
        ↓
BlockBreakEvent (Minecraft)
        ↓
Reward Engine (coins per ore)
        ↓
Hashing Engine (SHA-256 + difficulty)
        ↓
Blockchain Ledger (chain.txt)
        ↓
Economy & Wallets
        ↓
GUI (Leaderboard / NFT Shop)
```

---

## ✨ Features

### ⛏ Mining & Blockchain
- Proof-of-work hashing (SHA-256)  
- Dynamic difficulty  
- Linked blocks (previous hash → next hash)  
- Persistent blockchain ledger (`chain.txt`)

### 💰 Economy
- Coin rewards per ore  
- Persistent balances  
- Wallet addresses  
- Player-to-player payments

### 🪓 NFTs
- NFT Pickaxe with unique token ID  
- Purchased with in-game coins  
- Stored as unique items

### 🏆 GUI
- Leaderboard GUI showing top miners  
- Debug overlay in GUI title

### 🛠 Debug & Engineering
- `/debug` – toggle verbose logs  
- `/inspectchain` – view last block  
- `/verifychain` – validate blockchain integrity  

---

## 🧑‍💻 Tech Stack

- **Language:** Java  
- **Platform:** PaperMC / Spigot API  
- **Game Engine:** Minecraft  
- **Hashing:** SHA-256  
- **Storage:** Local file system  
- **UI:** Minecraft inventory GUI  

---

## ⚙️ Setup Instructions

1. Install Java 17+
2. Run PaperMC server
3. Copy MiningChain.jar into /plugins
4. Start server

## ▶️ Running the Paper Server

### Requirements

- Java 17 or higher
- PaperMC server JAR
- Minecraft Java Edition client

### Step 1: Download PaperMC

Download the latest Paper server for your Minecraft version from:
[https://papermc.io/downloads/paper](https://papermc.io/downloads/paper)

Rename the downloaded file to: `server.jar`

### Step 2: Create Server Folder

Create a new folder, for example: `mcserver`

Place `server.jar` inside it.

### Step 3: First Run

Open a terminal or PowerShell in the server folder and run:

```bash
java -jar server.jar
```

The server will stop and create an `eula.txt` file.

### Step 4: Accept EULA

Open `eula.txt` and change:

```
eula=false
```

to:

```
eula=true
```

Save the file.

### Step 5: Start Server

Run again:

```bash
java -jar server.jar
```

Wait until you see:

```
Done (x.xxxs)! For help, type "help"
```

This means the server is running.

### Step 6: Install Plugin

1. Copy `MiningChain.jar`
2. Paste it into: `mcserver/plugins/`
3. Restart the server

You should see:

```
[MiningChain] enabled!
```

### Step 7: Join Server

1. Open Minecraft → Multiplayer → Direct Connect
2. Enter: `localhost`

## 🎮 Commands

| Command | Purpose |
|---------|---------|
| `/wallet` | View wallet information |
| `/balance` | Check account balance |
| `/pay` | Send coins to another player |
| `/nft` | View or purchase NFT pickaxes |
| `/top` | View leaderboard |
| `/debug` | Toggle verbose debug logs |
| `/inspectchain` | Inspect the last block in the chain |
| `/verifychain` | Verify blockchain integrity |  

---

## 🌐 Future Scope (Quai Network Integration)

- Mining events → blockchain transactions
- NFT pickaxes → smart contracts
- Wallets → on-chain addresses
- Ledger → on-chain storage  

---

## 🏁 Conclusion

MiningChain demonstrates how blockchain mechanics can be embedded into interactive systems to make complex distributed concepts observable, testable, and engaging.

---

Built for **VibeCraft Hackathon**

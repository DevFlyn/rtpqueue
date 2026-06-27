# rtpqueue

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21+-brightgreen?style=for-the-badge" alt="Minecraft">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge" alt="Java">
  <img src="https://img.shields.io/badge/Mode-Ranked%201v1-red?style=for-the-badge" alt="Ranked 1v1">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="MIT License">
</p>

<p align="center">
  <strong>A competitive ranked PvP plugin featuring an Elo rating system, 1v1 matchmaking, RTP queue integration, and live leaderboards.</strong>
</p>

---
# RANKS
* COPPER 1-3
* IRON 1-3
* GOLD 1-3
* DIAMOND 1-3
* NETHERITE 1-3

## Features

* ⚔️ Competitive Elo rating system
* 🎯 Ranked 1v1 matchmaking
* 🌍 RTP Queue support
* 📈 Automatic Elo gain & loss
* 🏆 Top 10 leaderboard
* ⚡ Lightweight and optimized
* 💾 Persistent player statistics
* 🛠️ Administrator Elo management commands

---

## Commands

### Player Commands

| Command                 | Description                      |
| ----------------------- | -------------------------------- |
| `/ranked`               | Opens the Ranked menu.           |
| `/rq`                   | Alias of `/ranked`.              |
| `/rtpqueue`             | Alias of `/ranked`.              |
| `/ranked leaderboard`   | View the Top 10 Elo leaderboard. |
| `/rq leaderboard`       | Alias of leaderboard.            |
| `/rtpqueue leaderboard` | Alias of leaderboard.            |

### Admin Commands

| Command                                 | Description                            |
| --------------------------------------- | -------------------------------------- |
| `/rtpadmin`                             | Displays all available admin commands. |
| `/rtpadmin setelo <player> <elo>`       | Sets a player's Elo.                   |
| `/rtpadmin addelo <player> <amount>`    | Adds Elo to a player.                  |
| `/rtpadmin removeelo <player> <amount>` | Removes Elo from a player.             |

---

## How It Works

1. Join the Ranked Queue.
2. Get matched against another player.
3. Fight in a competitive 1v1.
4. Winning increases your Elo.
5. Losing decreases your Elo.
6. Climb the leaderboard and become the highest-ranked player.

---

## Leaderboard

Track the strongest players on your server using:

* `/ranked leaderboard`
* `/rq leaderboard`
* `/rtpqueue leaderboard`

Displays the **Top 10** players based on their current Elo.

---

## Configuration

Competitive Elo System is designed to be lightweight and easy to configure. Customize Elo values, queue behavior, messages, and other settings to match your server's competitive environment.

---

## Support

Found a bug or have a suggestion?

Please contact **FLYNZX** on Discord and include:

* Plugin version
* Server software and version
* Console errors (if any)
* Steps to reproduce the issue

---

## License

This project is licensed under the **MIT License**.

See the [`LICENSE`](LICENSE) file for more information.

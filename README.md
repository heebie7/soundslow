# Sound Slow

A Fabric client mod for **Minecraft 1.21.11** that lowers the pitch of every game sound.

## What it's for

Record at a low `/tick rate`, then speed the footage back up in a video editor and the audio
comes out at normal pitch and length. The trick is that pitch-based slowdown (a plain resample)
is the exact inverse of the editor's speed-up — **as long as pitch correction is OFF in the editor.**

## Usage (in-game)

- `/soundslow auto` — pitch multiplier follows the current `/tick rate` (tps / 20)
- `/soundslow 0.5` — fixed multiplier (range 0.01–2.0)
- `/soundslow off` — back to normal

## Install

Needs [Fabric Loader](https://fabricmc.net/use/) + [Fabric API](https://modrinth.com/mod/fabric-api) on a 1.21.11 instance.
Drop the `.jar` from the [latest release](../../releases/latest) into your `mods/` folder.

## Build

Cloud-built via GitHub Actions on every push. Jar appears under Releases → `latest`.

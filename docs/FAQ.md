# Frequently Asked Questions
### 1. Is this mod a "rat"?
No, it is not. The code is open-source and available on GitHub for anyone who wants to check themselves. It has also been cleared by both Rat Checker Discord and SkyClient.  
 
  General note: Don't ask creators of mod if a mod is malware, as that would be a self-defeating question.
2. Waypoints not showing up in dungeons, are showing up in the wrong room, or an error when trying to enter a room.

1. **Update Routes**: Click the "Update Routes" button.
2. **Check for `routes.json`**: There should be a `routes.json` file in the folder `.minecraft/config/SecretRoutes`. It includes all the waypoint data. If it doesn't exist, download from GitHub.
- Make sure it's named **exactly** `routes.json` (no numbers, no spaces), because it might get overwritten if downloaded multiple times.
3. **Skytils Integration**: Toggle the "Inject Fake Map" option of Skytils. Using such a setting would avoid a problem where dungeon maps would become replaced with an arrow when holding a bow, so preventing room detection from working accordingly .
### 3. The fire trail isn't appearing in dungeons.

**Fire particles** need to be enabled in your game settings. Since the trail consists of fire particles, those need to be on for the trail to show up.
### 4. Lines don't render as they should.

That's a known OpenGL issue and currently I'm looking into that but there's no fix yet.
### 5. Items aren't registered upon pickup.

Check if you have a **Personal Deletor** enabled for dungeon items. If set to delete dungeon floor items, **Hypixel won't send the item pickup packet**, thus making the `onPacketReceive` method never trigger.
### 6. Recording Your Own Routes

To record and add your own routes:
1. Record routes following this document.
2. Replace the `routes.json` file in your `.minecraft/config/SecretRoutes` folder with your recorded routes.
### 7. Cannot connect to servers

In the event you get "The servers are down for maintenance, just restart the game; this is one of the bugs, and usually it sorts out itself with a re-launch of the game.

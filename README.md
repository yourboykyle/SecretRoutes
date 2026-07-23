<h1 align="center">
  Secret Routes Mod
</h1>

<div align="center">

[![Modrinth Version](https://img.shields.io/modrinth/v/secret-routes-mod?style=for-the-badge&label=version)](https://modrinth.com/mod/secret-routes-mod)&nbsp;&nbsp;
[![Discord](https://img.shields.io/discord/1111306530357256262?label=discord&color=9089DA&logo=discord&style=for-the-badge)](https://discord.gg/qmtQmz4V3X)&nbsp;&nbsp;
[![Total Downloads](https://img.shields.io/github/downloads/yourboykyle/SecretRoutes/total?label=downloads&color=208a19&logo=github&style=for-the-badge)](https://github.com/yourboykyle/SecretRoutes/releases)&nbsp;&nbsp;
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/secret-routes-mod?label=downloads&color=208a19&logo=modrinth&style=for-the-badge)](https://modrinth.com/mod/secret-routes-mod)&nbsp;&nbsp;

</div>

---
<div align="center">
  
  [![Modrinth](https://img.shields.io/static/v1?label=Modrinth&message=download%20on%20modrinth&logo=modrinth&style=for-the-badge&color=208a19)](https://modrinth.com/mod/secret-routes-mod)
</div>

---

### Features
<details>
    <summary><strong>Dungeon Routes</strong></summary>
    <ul>
        <li>Some of the best routes on the internet for secrets </li>
        <li>Tracking line made by particles or OpenGL Render lines </li>
        <li>AOTV waypoints </li>
        <li>Interact waypoints</li>
        <li>Superboom waypoints</li>
        <li>Stonk waypoints</li>
        <li>Ender pearl waypoints/ Pearl launch angle lines </li>
        <li>Render entire route (instead of just one secret at a time)</li>
        <li>Render all secrets (Renders all secrets and levers in the room) </li>
        <li>Supports multiple routes in the same room (picks closest one)</li>
        <li>Render multiple steps at the same time</li>
        <li>Custom secret completion sounds</li>
        <li><strong>Custom Routes</strong>
            <ul>
                <li>Record your own custom routes: <a href="https://github.com/ChrisTechs/SRM-Route-Recorder">ChrisTechs/SRM-Route-Recorder</a></li>
                <li>Easy Scoreboard display for some recording information</li>
                <li>Easily import routes created by others</li>
            </ul>
        </li>
        <li><strong>Customizable</strong>
            <ul>
                <li>Recolor every single OpenGL rendering item</li>
                <li>Toggle on/off all waypoint types individually</li>
                <li>Recolor all text boxes to any of Minecraft's 16 colors</li>
                <li>Change between particle lines, OpenGL rendered line, or no lines</li>
                <li>Save and load different color profiles</li>
                <li>Import profiles from others easily</li>
            </ul>
        </li>
    </ul>
</details>

<details>
    <summary><strong>Interface</strong></summary>
    <ul>
        <li>Configurable with an organised YetAnotherConfigLib interface to easily find all features</li>
    </ul>
</details>

<details>
    <summary><strong>General</strong></summary>
    <ul>
        <li>Personal best tracking for room routes</li>
        <li>Auto inform new updates (Toggle on by default)</li>
        <li>Auto download new updates (Toggled off by default)</li>
    </ul>
</details>
<details>
    <summary><strong>Bridge</strong></summary>
    <ul>
        <li>Make bridge bot messages look nicer in SRM Guild <em>(/g join srm)</em></li>
    </ul>
</details>


#### More to come...

---
### Commands
<ul>
    <li><strong>/srm</strong> -> Opens main config menu</li>
    <li><strong>/loadroute</strong> -> Loads the current route from the downloads folder</li>
    <li>
        <details>
        <summary><strong>/recording</strong> -> <em>All of these are also buttons in the config menu</em></summary>
        <ul>
            <li><strong>start</strong> -> Starts route recording process</li>
            <li><strong>stop</strong> -> Stops route recording process</li>
            <li><strong>export</strong> -> Exports the recorded routes to the routes.json file in your downloads</li>
            <li><strong>getroom</strong> -> Sends a chat message with information about the current room</li>
            <li><strong>setbat</strong> -> Sets a bat in the current secret route</li>
            <li><strong>setexit</strong> -> Sets an exit waypoint in the route, and stops recording</li>
            <li><strong>import</strong> -> Imports routes from the downloads route folder into memory</li>
        </ul>
        </details>
    </li>
    <li>
        <details>
            <summary><strong>/changecolorprofile</strong> -> <em>also some buttons</em></summary>
            <ul>
                <li><strong>list</strong> -> Lists all files in the ColorProfiles directory</li>
                <li><strong>load</strong> -> Loads a color profile with the name of the following argument (loads default if no argument specified)</li>
                <li><strong>save</strong> -> Saves the currently selected options to a file with the name specified</li>
            </ul>
        </details>
    </li>
    <li>
        <details>
            <summary><strong>/changeroute</strong> -> <em>also some buttons</em></summary>
            <ul>
                <li><strong>list</strong> -> Lists all files in the Routes directory</li>
                <li><strong>load</strong> -> Loads a color profile with the name of the following argument (loads default if no argument specified)</li>
            </ul>
        </details>
    </li>
    <li>
        <details>
            <summary><strong>/srmdebug</strong> -> <em>some debug commands and variable modification</em></summary>
            <ul>
                <li><strong>lever</strong> -> Sends some info about the rendered locked chest lever</li>
                <li><strong>pos</strong> -> Send info about current player position</li>
                <li><strong>var</strong> -> print the current value of a variable, or change it to the value of the next argument</li>
            </ul>
        </details> 
    </li>
</ul>

---
### Instructions:
- Download the latest release: https://modrinth.com/mod/secret-routes-mod
- Put the .jar in your `.minecraft/mods` folder
- Watch the video for instructions: https://youtu.be/p3-KPTbXWFw
- If you have any questions, join the discord: [https://discord.gg/qmtQmz4V3X](https://discord.gg/qmtQmz4V3X)
---
### Links:

Modrinth: https://modrinth.com/mod/secret-routes-mod

Discord: [https://discord.gg/qmtQmz4V3X](https://discord.gg/qmtQmz4V3X)

**Support the mod**: https://buymeacoffe.com/SecretRoutes

---

### Credits:
- @yourboykyle (me) - developer (yourboykyle)
- @wyannnnn - developer (R-aMcC)
- @johnd_ - supplied very nice reference code for help translating to 1.21, check out his mod here: https://modrinth.com/mod/hunchclient
- @mrfast - help with various things
- @zzyyrraa - recorded routes + meows for custom secret noises
- @itplays - modern pearlroutes
- https://github.com/Quantizr/DungeonRoomsMod - code for room detection
- https://github.com/Soopyboo32/SoopyV2 - code for some rendering functions (translated from JS to java)
- https://github.com/Dungeons-Guide/Skyblock-Dungeons-Guide - code for some rendering functions
- https://github.com/odtheking/Odin - copied a small bit of code for playing sounds cus wyan is lazy lol

<h1 align="center">
  Secret Routes Mod
</h1>

<div align="center">

[![Modrinth Version](https://img.shields.io/modrinth/v/secret-routes-mod?style=for-the-badge&label=version)](https://modrinth.com/mod/secret-routes-mod)&nbsp;&nbsp;
[![Discord](https://img.shields.io/discord/1111306530357256262?label=discord&color=9089DA&logo=discord&style=for-the-badge)](https://discord.gg/secretroutes)&nbsp;&nbsp;
[![Total Downloads](https://img.shields.io/github/downloads/yourboykyle/SecretRoutes/total?label=downloads&color=208a19&logo=github&style=for-the-badge)](https://github.com/yourboykyle/SecretRoutes/releases)&nbsp;&nbsp;
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/secret-routes-mod?label=downloads&color=208a19&logo=modrinth&style=for-the-badge)](https://modrinth.com/mod/secret-routes-mod)&nbsp;&nbsp;

</div>

---
[![Modrinth](https://img.shields.io/static/v1?label=Modrinth&message=download%20on%20modrinth&logo=modrinth&style=for-the-badge&color=208a19)](https://modrinth.com/mod/secret-routes-mod)

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
        <li><strong><em>(NEW)</em></strong> Supports multiple routes in the same room (picks closest one)</li>
        <li> 
            <details>
                <summary><strong>Custom Routes</strong></summary>
                <ul>
                    <li>Record your own custom routes mostly automatically</li>
                    <li>Easy HUD display for some recording information</li>
                    <li>Easily import routes created by others</li>
                </ul>
            </details>
        </li>
        <li>
            <details>
                <summary><strong>Customizable</strong></summary>
                <ul>
                    <li>Recolor every single OpenGL rendering item</li>
                    <li>Toggle on/off all waypoint types individually</li>
                    <li>Recolor all text boxes to any of Minecraft's 16 colors</li>
                    <li>Change between particle lines, OpenGL rendered line, or no lines</li>
                    <li>Save and load different color profiles</li>
                    <li>Import profiles from others easily</li>
                </ul>
            </details>
        </li>
    </ul>
</details>

<details>
    <summary><strong>Interface</strong></summary>
    <ul>
        <li>Configurable with a beautifully organised OneConfig interface to easily find all features</li>
    </ul>
</details>

<details>
    <summary><strong>QOL</strong></summary>
    <ul>
        <li>Auto inform new updates (Toggle on by default)</li>
        <li>Auto download new updates (Toggled off by default)</li>
    </ul>
</details>
<details>
    <summary><strong>Messages</strong></summary>
    <ul>
        <details>
            <summary>Boss message hider</summary>
            <ul>
                <li>Individually control which boss' messages to hide and show</li>
                <li><Strong>Does not impact other mods that use boss messages for timing</Strong></li>
            </ul>
        </details>
         <details>
            <summary>Blood spawned notification</summary>
            <ul>
                <li>Customizable message to display when all blood mobs have spawned</li>
                <li>Custom color</li>
                <li>Custom duration</li>
                <li>Custom position</li>
                <li>Custom text</li>
            </ul>
        </details>
    </ul>
</details>

<details>
    <summary><strong><em>(NEW)</em></strong> <strong>Bridge</strong></summary>
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
                <li><strong>bloodtime</strong> -> display blood ready message for that many milis</li>
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
- If you have any questions, join the discord: https://discord.gg/secretroutes
---
### Links:

Modrinth: https://modrinth.com/mod/secret-routes-mod

Discord: [discord.gg/secretroutes](https://discord.com/channels/1111306530357256262/1111670971485663271)

---

### Credits:
- @yourboykyle (me) - developer
- @wyannnnn - developer
- @mrfast - help with various things
- @zzyyrraa - recorded routes + meows for custom secret noises
- https://github.com/Quantizr/DungeonRoomsMod - code for room detection
- https://github.com/Soopyboo32/SoopyV2 - code for some rendering functions (translated from JS to java)
- https://github.com/Dungeons-Guide/Skyblock-Dungeons-Guide - code for some rendering functions
- https://github.com/odtheking/Odin - copied a small bit of code for playing sounds cus wyan is lazy lol

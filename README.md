# Shop Auto Drop — Fabric 1.21.1

Client-side Fabric mod prototype for Minecraft Java 1.21.1.

## Current behavior
- Press **O** to toggle Auto Drop ON/OFF.
- While a screen is open, the mod watches for increases in the player's total **Bone** or **Bone Meal** count and attempts to drop the newly added amount without closing the screen.
- Fabric API is required.

## Build a JAR on GitHub (no local build)
1. Open the **Actions** tab.
2. Select **Build Shop Auto Drop JAR**.
3. Open the latest run. If needed, use **Run workflow**.
4. After a successful run, scroll to **Artifacts** and download **ShopAutoDrop-Fabric-1.21.1**.
5. Extract the downloaded artifact ZIP to get the JAR file.

## Important limitations
This is an untested prototype. It detects inventory count increases, not the shop's specific purchase button, so Bone/Bone Meal obtained from another source while a GUI is open may also be dropped. Server-side rules or custom shop implementations may prevent the drop action. Check the Actions build result before installing.

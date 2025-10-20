package com.ethem00.idogmod.entity.client.gui.screen.ingame;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import com.ethem00.idogmod.network.ModPackets;
import com.ethem00.idogmod.screen.iDogScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class iDogScreen extends HandledScreen<iDogScreenHandler> {
    private static final Identifier SCREEN_TEXTURE = new Identifier(iDogMod.MOD_ID, "textures/gui/container/idog_screen.png");
    private final iDogEntity idog;
    private float mouseX;
    private float mouseY;

    public iDogScreen(iDogScreenHandler handler, PlayerInventory inventory, Text text) {
        super(handler, inventory, handler.getEntity().getDisplayName());
        this.idog = handler.getEntity();
        this.backgroundWidth = 176;
        this.backgroundHeight = 237;
        this.playerInventoryTitleX = 136;
        this.playerInventoryTitleY = this.backgroundHeight - 107;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(SCREEN_TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);


        InventoryScreen.drawEntity(context, i + 124, j + 114, 24, i + 51 - this.mouseX, j + 75 - 50 - this.mouseY, this.idog);

        float m = this.idog.getSongVolume();
        if (m > 0) { //Draw volume meter with speaker unmuted
            int n = ((int) (m * 106));
            if (n > 0) {

                int nMod = Math.abs((n - 106));
                //Dynamically change from starting Y=33 to end Y=138. 106 means max.
                context.drawTexture(SCREEN_TEXTURE, i + 27, j + nMod + 33, 176, nMod, 32, n); //Volume Bar
            }

        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        context.drawText(this.textRenderer, "Hotbar", this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Environment(EnvType.CLIENT)
    private void typeBasedPacketSwitch(int type) {
        switch(type) {
            case -10 -> this.sendButtonPacket(-10);   //Vol -10 Packet
            case -5 -> this.sendButtonPacket(-5);     //Vol -5 Packet
            case 5 -> this.sendButtonPacket(5);       //Vol +5 Packet
            case 10 -> this.sendButtonPacket(10);     //Vol +10 Packet
            //------------------------------------------------
            case 1 -> this.sendButtonPacket(1);       //Vol MAX Packet
            case -1 -> this.sendButtonPacket(-1);     //Vol ZERO Packet
            case 2 -> this.sendButtonPacket(2);       //Loop ON Packet
            case -2 -> this.sendButtonPacket(-2);     //Loop OFF Packet
            case 3 -> this.sendButtonPacket(3);       //Alerts ON Packet
            case -3 -> this.sendButtonPacket(-3);     //Alerts OFF Packet
            //Warning
            default -> System.out.println("Non-compliant type attempt of: " + type);
        }
    }

    @Environment(EnvType.CLIENT)
    private void sendButtonPacket(int packetType) {
        if (this.client == null || this.idog == null) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.idog.getId());   // Send the iDog entity ID
        buf.writeInt(packetType);          // Send the type (-10, +10, etc.)

        //System.out.println("Packet of " + packetType + " being sent by entity " + this.idog.getId());

        ClientPlayNetworking.send(ModPackets.IDOG_BUTTON_PACKET, buf);
    }

    //Buttons and widgets
    @Override
    protected void init() {
        super.init();
        // Minecraft calculates these automatically
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;

        //Volume Buttons
        addDrawableChild(new iDogScreenWidget(this, x + 8, y + 16, 208, 160, 16, 16, Text.empty(), -10) {});
        addDrawableChild(new iDogScreenWidget(this, x + 26, y + 16, 208, 128, 16, 16, Text.empty(), -5) {});
        addDrawableChild(new iDogScreenWidget(this, x + 44, y + 16, 208, 96, 16, 16, Text.empty(), 5) {});
        addDrawableChild(new iDogScreenWidget(this, x + 62, y + 16, 208, 64, 16, 16, Text.empty(), 10) {});
        //Mute
        addDrawableChild(new iDogSpeakerWidget(this, x + 80, y + 16, 208, 0, 16, 16, Text.empty(), idog.getSongVolume() > 0 ? 1 : -1) {});
        //Loop
        addDrawableChild(new iDogStateWidget(this, x + 98, y + 16, 224, 0, 32, 16, Text.empty(), idog.getLoopBool() ? 2 : -2) {});
        //Alert
        addDrawableChild(new iDogStateWidget(this, x + 132, y + 16, 224, 64, 32, 16, Text.empty(), idog.getAlertBool() ? 3 : -3) {});
    }

    @Environment(EnvType.CLIENT)
    abstract static class iDogSpeakerWidget extends iDogStateWidget {
        public iDogSpeakerWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Text message, int buttonType) {
            super(screen, x, y, u, v, width, height, message, buttonType);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) { //On
                if(screen.idog.getSongVolume() > 0) {
                    context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);} else {
                    context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 32, width, height);}
            } else { //Off
                if(type > 0) {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);}
                else {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 48, width, height);}
                this.waitTime++;
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.waitTime = 0;

            if(screen.idog.getSongVolume() > 0) {this.type = -1;}
            else {this.type = 1;}
            screen.typeBasedPacketSwitch(type);
        }
    }

    @Environment(EnvType.CLIENT)
    abstract static class iDogStateWidget extends iDogScreenWidget {
        public iDogStateWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Text message, int buttonType) {
            super(screen, x, y, u, v, width, height, message, buttonType);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) { //On
                if(type > 0) {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);}
                else {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 32, width, height);}
            } else { //Off
                if(type > 0) {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);}
                else {context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 48, width, height);}
                this.waitTime++;
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.waitTime = 0;
            this.type = this.type*-1;
            screen.typeBasedPacketSwitch(type);
        }
    }

    @Environment(EnvType.CLIENT)
    abstract static class iDogScreenWidget extends ClickableWidget implements iDogScreen.iDogButtonWidget {
        protected int u;
        protected int v;
        protected int type;
        protected final iDogScreen screen;
        protected int waitTime;

        public iDogScreenWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Text message, int buttonType) {
            super(x, y, width, height, Text.empty());
            this.u = u;
            this.v = v;
            this.type = buttonType;
            this.screen = screen;
            this.waitTime = 10;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) {
                context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);
            } else {
                context.drawTexture(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);
                this.waitTime++;
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.waitTime = 0;
            screen.typeBasedPacketSwitch(type);
        }

        @Override
        public void tick(int level) {
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }
    }

    @Environment(EnvType.CLIENT)
    interface iDogButtonWidget {
        void tick(int level);
    }
}

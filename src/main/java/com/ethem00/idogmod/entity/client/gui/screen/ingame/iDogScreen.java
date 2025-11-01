package com.ethem00.idogmod.entity.client.gui.screen.ingame;

import com.ethem00.idogmod.entity.iDogEntity;
import com.ethem00.idogmod.iDogMod;
import com.ethem00.idogmod.network.ModPackets;
import com.ethem00.idogmod.network.iDogButtonPressedPacketC2S;
import com.ethem00.idogmod.screen.iDogScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class iDogScreen extends AbstractContainerScreen<iDogScreenHandler> {
    private static final ResourceLocation SCREEN_TEXTURE = ResourceLocation.fromNamespaceAndPath(iDogMod.MOD_ID, "textures/gui/container/idog_screen.png");
    private final iDogEntity idog;
    private float mouseX;
    private float mouseY;

    public iDogScreen(iDogScreenHandler handler, Inventory inventory, Component text) {
        super(handler, inventory, handler.getEntity().getDisplayName());
        this.idog = handler.getEntity();
        this.imageWidth = 176;
        this.imageHeight = 237;
        this.inventoryLabelX = 136;
        this.inventoryLabelY = this.imageHeight - 107;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(SCREEN_TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);


        InventoryScreen.renderEntityInInventoryFollowsMouse(context, i + 124, j + 114, 24, i + 51 - this.mouseX, j + 75 - 50 - this.mouseY, this.idog);

        float m = this.idog.getSongVolume(true);
        if (m > 0) { //Draw volume meter with speaker unmuted
            int n = ((int) (m * 106));
            if (n > 0) {

                int nMod = Math.abs((n - 106));
                //Dynamically change from starting Y=33 to end Y=138. 106 means max.
                context.blit(SCREEN_TEXTURE, i + 27, j + nMod + 33, 176, nMod, 32, n); //Volume Bar
            }

        }
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        context.drawString(this.font, "Hotbar", this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @OnlyIn(Dist.CLIENT)
    private void typeBasedPacketSwitch(int type) {
        switch(type) {
            case -10 -> this.sendButtonPacket(-10);     //Vol -10 Packet
            case -5 -> this.sendButtonPacket(-5);       //Vol -5 Packet
            case 5 -> this.sendButtonPacket(5);         //Vol +5 Packet
            case 10 -> this.sendButtonPacket(10);       //Vol +10 Packet
            //------------------------------------------------
            case 1 -> this.sendButtonPacket(1);         //Vol MAX Packet
            case -1 -> this.sendButtonPacket(-1);       //Vol ZERO Packet
            case 2 -> this.sendButtonPacket(2);         //Loop ON Packet
            case -2 -> this.sendButtonPacket(-2);       //Loop OFF Packet
            case 3 -> this.sendButtonPacket(3);         //Alerts ON Packet
            case -3 -> this.sendButtonPacket(-3);       //Alerts OFF Packet
            //-------------------------------------------------
            case 4 -> this.sendButtonPacket(4);         //EJECT Disc Packet
            //Warning
            default -> System.out.println("Non-compliant type attempt of: " + type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void sendButtonPacket(int packetType) {
        if (!this.idog.level().isClientSide || this.idog == null) return;

        ModPackets.CHANNEL.sendToServer(
                new iDogButtonPressedPacketC2S(this.idog.getId(), packetType));
    }

    //Buttons and widgets
    @Override
    protected void init() {
        super.init();
        // Minecraft calculates these automatically
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        //Volume Buttons
        addRenderableWidget(new iDogScreenWidget(this, leftPos + 8, topPos + 16, 208, 160, 16, 16, Component.empty(), -10) {});
        addRenderableWidget(new iDogScreenWidget(this, leftPos + 26, topPos + 16, 208, 128, 16, 16, Component.empty(), -5) {});
        addRenderableWidget(new iDogScreenWidget(this, leftPos + 44, topPos + 16, 208, 96, 16, 16, Component.empty(), 5) {});
        addRenderableWidget(new iDogScreenWidget(this, leftPos + 62, topPos + 16, 208, 64, 16, 16, Component.empty(), 10) {});
        //Mute
        addRenderableWidget(new iDogSpeakerWidget(this, leftPos + 80, topPos + 16, 208, 0, 16, 16, Component.empty(), idog.getSongVolume(true) > 0 ? 1 : -1) {});
        //Loop
        addRenderableWidget(new iDogStateWidget(this, leftPos + 98, topPos + 16, 224, 0, 32, 16, Component.empty(), idog.getLoopBool() ? 2 : -2) {});
        //Alert
        addRenderableWidget(new iDogStateWidget(this, leftPos + 132, topPos + 16, 224, 64, 32, 16, Component.empty(), idog.getAlertBool() ? 3 : -3) {});
        //EJECT
        addRenderableWidget(new iDogEjectWidget(this, leftPos + 97, topPos + 51, 202, 192, 54, 18, Component.empty(), 4) {});
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class iDogEjectWidget extends iDogScreenWidget {
        public iDogEjectWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Component message, int buttonType) {
            super(screen, x, y, u, v, width, height, message, buttonType);
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture

            if(screen.idog.getDiscAsItem() instanceof RecordItem) {
                if(this.waitTime >= 10) { //On
                    context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);
                } else { //Off
                    context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + height, width, height);
                }
            } else {
                context.blit(SCREEN_TEXTURE, this.getX() + 8008, this.getY() + 8008, u, v, width, height);
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.waitTime = 0;

            if(screen.idog.getDiscAsItem() instanceof RecordItem) {

                this.playDownSound(Minecraft.getInstance().getSoundManager());
                screen.typeBasedPacketSwitch(type);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.active && this.visible) {
                if (this.isValidClickButton(button)) {
                    boolean bl = this.clicked(mouseX, mouseY);
                    if (bl) {
                        this.onClick(mouseX, mouseY);
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class iDogSpeakerWidget extends iDogStateWidget {
        public iDogSpeakerWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Component message, int buttonType) {
            super(screen, x, y, u, v, width, height, message, buttonType);
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) { //On
                if(screen.idog.getSongVolume(true) > 0) {
                    context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);} else {
                    context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 32, width, height);}
            } else { //Off
                if(type > 0) {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);}
                else {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 48, width, height);}
                this.waitTime++;
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.waitTime = 0;

            if(screen.idog.getSongVolume(true) > 0) {this.type = -1;}
            else {this.type = 1;}
            screen.typeBasedPacketSwitch(type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    abstract static class iDogStateWidget extends iDogScreenWidget {
        public iDogStateWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Component message, int buttonType) {
            super(screen, x, y, u, v, width, height, message, buttonType);
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) { //On
                if(type > 0) {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);}
                else {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 32, width, height);}
            } else { //Off
                if(type > 0) {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);}
                else {context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 48, width, height);}
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

    @OnlyIn(Dist.CLIENT)
    abstract static class iDogScreenWidget extends AbstractWidget implements iDogScreen.iDogButtonWidget {
        protected int u;
        protected int v;
        protected int type;
        protected final iDogScreen screen;
        protected int waitTime;

        public iDogScreenWidget(iDogScreen screen, int x, int y, int u, int v, int width, int height, Component message, int buttonType) {
            super(x, y, width, height, Component.empty());
            this.u = u;
            this.v = v;
            this.type = buttonType;
            this.screen = screen;
            this.waitTime = 10;
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            // Draws a section of the GUI texture instead of a button texture
            if(this.waitTime >= 10) {
                context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v, width, height);
            } else {
                context.blit(SCREEN_TEXTURE, this.getX(), this.getY(), u, v + 16, width, height);
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
        protected void updateWidgetNarration(NarrationElementOutput builder) {

        }
    }

    @OnlyIn(Dist.CLIENT)
    interface iDogButtonWidget {
        void tick(int level);
    }
}

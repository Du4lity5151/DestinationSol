package org.destinationsol;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.music.OggMusicManager;
import org.destinationsol.assets.sound.OggSoundManager;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.screens.RightPaneLayout;
import org.destinationsol.menu.MenuLayout;
import org.destinationsol.modules.ModuleManager;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.SolLayouts;
import org.destinationsol.ui.UiDrawer;
import org.terasology.context.Lifetime;
import org.terasology.gestalt.di.ServiceRegistry;
import org.terasology.gestalt.entitysystem.component.management.ComponentManager;

public class CoreService extends ServiceRegistry {
    public CoreService(SolApplication application) {
        this.with(SolApplication.class).lifetime(Lifetime.Singleton).use(() -> application);
        this.with(CommonDrawer.class).lifetime(Lifetime.Singleton);
        this.with(GameOptions.class).lifetime(Lifetime.Singleton).use(() -> {
            boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS;
            if (application.isMobile()) {
                DebugOptions.read(null);
            }
            return new GameOptions(isMobile, null);
        });
        this.with(ModuleManager.class).lifetime(Lifetime.Singleton);

        this.with(OggMusicManager.class).lifetime(Lifetime.Singleton);
        this.with(OggSoundManager.class).lifetime(Lifetime.Singleton);
        this.with(SolInputManager.class).lifetime(Lifetime.Singleton);

        this.with(ComponentManager.class).lifetime(Lifetime.Singleton).use(ComponentManager::new);
        this.with(AssetHelper.class).lifetime(Lifetime.Singleton).use(AssetHelper::new);

        this.with(DisplayDimensions.class).lifetime(Lifetime.Singleton).use(() -> new DisplayDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        this.with(UiDrawer.class).lifetime(Lifetime.Singleton);
        this.with(SolLayouts.class).lifetime(Lifetime.Singleton);
        this.with(RightPaneLayout.class).lifetime(Lifetime.Singleton);
        this.with(MenuLayout.class).lifetime(Lifetime.Singleton);
    }
}

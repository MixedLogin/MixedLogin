package fun.iiii.mixedlogin.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import fun.iiii.mixedlogin.MixedLoginMain;
import fun.iiii.mixedlogin.type.OfflineUUIDType;
import fun.iiii.mixedlogin.util.ExtraUuidUtils;
import fun.iiii.openvelocity.api.event.connection.BackendEncryptRequestEvent;
import fun.iiii.openvelocity.api.event.connection.OpenPreLoginEvent;
import java.util.UUID;

public class EventListener {
  @Subscribe
  public void onChoose(PlayerChooseInitialServerEvent event) {
//        判断是否为离线，离线的话则锁定服务器
    MixedLoginMain.getInstance().getLogger().info("触发选择服务器事件 玩家: " + event.getPlayer().getUsername());

  }

  @Subscribe
  public void onBackendLogin(BackendEncryptRequestEvent event) {
    MixedLoginMain.getInstance().getLoginServerManager().startSubRequest(event.getServerId(), event.getGameProfile());
    event.setSuccess(true);
  }

  @Subscribe
  public void onPreLogin(OpenPreLoginEvent event) {
    UUID uuid = event.getUuid();
    String name = event.getUserName();
    String host = event.getHost();
    OfflineUUIDType offlineUUIDType = ExtraUuidUtils.matchType(uuid, name);

    boolean offlineHost = MixedLoginMain.getInstance().getLoginServerManager().shouldOfflineHost(host);
    if (offlineHost) {
      MixedLoginMain.getInstance().getLogger().info("匹配到离线host 玩家: " + name);
    }
    if (offlineUUIDType != OfflineUUIDType.UNKNOWN) {
      MixedLoginMain.getInstance().getLogger().info("匹配到离线uuid 玩家: " + name + " 类型: " + offlineUUIDType);
    }
    if (offlineUUIDType != OfflineUUIDType.UNKNOWN || offlineHost) {
      String serverId = MixedLoginMain.getInstance().getLoginServerManager().startOfflineRequest(name);
      event.setServerId(serverId);
      event.setOnline(false);
    } else {
      event.setOnline(true);
    }
  }

}

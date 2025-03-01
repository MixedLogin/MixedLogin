package fun.iiii.mixedlogin.util;

import fun.iiii.mixedlogin.type.OfflineUUIDType;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ExtraUuidUtils {
  private static final UUID none = UUID.fromString("00000000-0000-0000-0000-000000000000");

  public static OfflineUUIDType matchType(UUID holderUUID, String name) {
    if (holderUUID == null) {
      return OfflineUUIDType.NONE;
    }
    if (holderUUID.equals(getNormalOfflineUUID(name))) {
      return OfflineUUIDType.OFFLINE;
    }
    if (holderUUID.equals(getPCL2UUID(name))) {
      return OfflineUUIDType.PCL;
    }
    if (holderUUID.equals(none)) {
      return OfflineUUIDType.NONE;
    }
    return OfflineUUIDType.UNKNOWN;
  }

  private static UUID getNormalOfflineUUID(String username) {
    return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
  }

  private static UUID getPCL2UUID(String username) {
    return UUID.fromString(toUUID(PCL2_UUIDStr(username)));
  }

  private static String PCL2_UUIDStr(String name) {
    String fullUuid = PCL2_strFill(Integer.toHexString(name.length()), "0", 16)
        + PCL2_strFill(Long.toHexString(PCL2_getHash(name)), "0", 16);
    return fullUuid.substring(0, 12) + "3"
        + fullUuid.substring(13, 16) + "9"
        + fullUuid.substring(17, 32);
  }

  private static String PCL2_strFill(String str, String code, int length) {
    if (str.length() > length) {
      return str.substring(0, length);
    }
    return code.repeat(length - str.length()) + str;
  }

  private static long PCL2_getHash(String str) {
    long hash = 5381;
    for (int i = 0; i < str.length(); i++) {
      hash = (hash << 5) ^ hash ^ (long) str.charAt(i);
    }
    return hash ^ 0xA98F501BC684032FL;
  }

  private static String toUUID(String no_) {
    String fullUuid = no_.substring(0, 8) + "-"
        + no_.substring(8, 12) + "-"
        + no_.substring(12, 16) + "-"
        + no_.substring(16, 20) + "-"
        + no_.substring(20, 32);
    return fullUuid;
  }

}

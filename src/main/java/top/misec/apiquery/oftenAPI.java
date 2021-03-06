package top.misec.apiquery;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import top.misec.login.Verify;
import top.misec.utils.HttpUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * 部分API简单封装。
 *
 * @author Junzhou Liu
 * @create 2020/10/14 14:27
 */
public class oftenAPI {
    static Logger logger = (Logger) LogManager.getLogger(oftenAPI.class.getName());

    /**
     * @return 返回主站查询到的硬币余额，查询失败返回0.0
     */
    public static Double getCoinBalance() {
        JsonObject responseJson = HttpUtil.doGet(ApiList.getCoinBalance);
        int responseCode = responseJson.get("code").getAsInt();

        JsonObject dataObject = responseJson.get("data").getAsJsonObject();

        if (responseCode == 0) {
            if (dataObject.get("money").isJsonNull()) {
                return 0.0;
            } else {
                return dataObject.get("money").getAsDouble();
            }
        } else {
            logger.debug("请求硬币余额接口错误，请稍后重试。错误请求信息：" + responseJson);
            return 0.0;
        }
    }

    /**
     * @param type 1大会员B币券  2 大会员福利
     */
    public static void vipPrivilege(int type) {
        String requestBody = "type=" + type
                + "&csrf=" + Verify.getInstance().getBiliJct();
        JsonObject jsonObject = HttpUtil.doPost(ApiList.vipPrivilegeReceive, requestBody);
        int responseCode = jsonObject.get("code").getAsInt();
        if (responseCode == 0) {
            if (type == 1) {
                logger.info("领取年度大会员每月赠送的B币券成功");
            } else if (type == 2) {
                logger.info("领取大会员福利/权益成功");
            }

        } else {
            logger.debug("领取年度大会员每月赠送的B币券/大会员福利失败，原因: " + jsonObject.get("message").getAsString());
        }
    }

    /**
     * 请求关注列表更新的视频，随机返回一个bvid
     *
     * @return bvid
     */
    public static String queryDynamicNew() {

        ArrayList<String> arrayList = new ArrayList();
        String urlParameter = "?uid=" + Verify.getInstance().getUserId()
                + "&type_list=8"
                + "&from="
                + "&platform=web";
        JsonObject jsonObject = HttpUtil.doGet(ApiList.queryDynamicNew + urlParameter);
        JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("cards");

        if (jsonArray != null) {
            for (JsonElement videoInfo : jsonArray) {
                JsonObject tempObject = videoInfo.getAsJsonObject().getAsJsonObject("desc");
                arrayList.add(tempObject.get("bvid").getAsString());
            }
        }

        Random random = new Random();
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    public static String videoTitle(String aid) {
        String title = null;
        String author = null;
        String urlParameter = "?aid=" + aid;
        JsonObject jsonObject = HttpUtil.doGet(ApiList.videoView + urlParameter);
        author = jsonObject.getAsJsonObject("data").getAsJsonObject("owner").get("name").getAsString();
        title = jsonObject.getAsJsonObject("data").get("title").getAsString();
        return author + " : " + title;
    }

}

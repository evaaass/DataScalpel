package cn.superhuang.data.scalpel.admin;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebApiFetcher {
    private static final String LOGIN_URL = "https://portal.iws-schiphol.nl/login";
    private static final String API_URL = "https://portal.iws-schiphol.nl/api.php?action=get_pre_alerts_overview&time=1752393893490&limit=500&_=1752393893170";

    public static void main(String[] args) {
        try {
            // 1. 创建登录连接
            URL loginUrl = new URL(LOGIN_URL);
            HttpURLConnection loginConn = (HttpURLConnection) loginUrl.openConnection();

            // 2. 设置登录请求参数
            loginConn.setRequestMethod("POST");
            loginConn.setDoOutput(true);
            loginConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 3. 发送登录请求（需要替换为实际登录参数）
            String loginParams = "username=LILYIMP&password=imp601";
            loginConn.getOutputStream().write(loginParams.getBytes());

            // 4. 获取PHPSESSID
            Map<String, List<String>> headers = loginConn.getHeaderFields();
            String cookie = headers.get("Set-Cookie").get(0);
            String phpsessid = cookie.split(";")[0];

            // 5. 创建API连接
            URL apiUrl = new URL(API_URL);
            HttpURLConnection apiConn = (HttpURLConnection) apiUrl.openConnection();

            // 6. 设置API请求参数（携带PHPSESSID）
            apiConn.setRequestMethod("GET");
            apiConn.setRequestProperty("Cookie", phpsessid);

            // 7. 获取API响应
            int responseCode = apiConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(apiConn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 8. 输出JSON数据
                System.out.println(response.toString());
            } else {
                System.out.println("API请求失败，响应码: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

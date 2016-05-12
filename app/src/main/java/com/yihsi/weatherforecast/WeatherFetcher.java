package com.yihsi.weatherforecast;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yihsi on 4/28/16.
 */
public class WeatherFetcher {
    private static final char last2bytes = (char)Integer.parseInt("00000011", 2);
    private static final char last4bytes = (char)Integer.parseInt("00001111", 2);
    private static final char last6bytes = (char)Integer.parseInt("00111111", 2);
    private static final char lead2bytes = (char)Integer.parseInt("11000000", 2);
    private static final char lead4bytes = (char)Integer.parseInt("11110000", 2);
    private static final char lead6bytes = (char)Integer.parseInt("11111100", 2);

    private static final char[] encodeTable = new char[] {'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'};

    private Context mContext;

    private static String standardUrlEncoder(String data, String key) {
        byte[] byteHAMC = null;
        String urlEncoder = "";
        try {
            // Mac provides the functionality of a "Message Authentication Code" (MAC) algorithm.
            Mac mac = Mac.getInstance("HmacSHA1");
            // SecretKeySpec specifies a secret key in a provider-independent fashion.
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            mac.init(spec);
            byteHAMC = mac.doFinal(data.getBytes());
            if (byteHAMC != null) {
                String oauth = encode(byteHAMC);
                urlEncoder = URLEncoder.encode(oauth, "UTF-8");
            }
        } catch (InvalidKeyException ike) {
            Log.e("standardUrlEncoder", "InvalidKeyException", ike);
        } catch (NoSuchAlgorithmException nae) {
            Log.e("standardUrlEncoder", "NoSuchAlgorithmException", nae);
        } catch (UnsupportedEncodingException uee) {
            Log.e("standardUrlEncoder", "UnsupportedEncodingException", uee);
        }

        return urlEncoder;
    }

    private static String encode(byte[] from) {
        StringBuilder to = new StringBuilder((int)(from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        for (int i = 0; i < from.length; i++) {
            num = num % 8;
            while (num < 8) {
                switch (num) {
                    case 0:
                        currentByte = (char)(from[i] & lead6bytes);
                        currentByte = (char)(currentByte >>> 2);
                        break;
                    case 2:
                        currentByte = (char)(from[i] & last6bytes);
                        break;
                    case 4:
                        currentByte = (char)(from[i] & last4bytes);
                        currentByte = (char)(currentByte << 2);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead2bytes) >>> 6;
                        }
                        break;
                    case 6:
                        currentByte = (char)(from[i] & last2bytes);
                        currentByte = (char)(currentByte << 4);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead4bytes) >>> 4;
                        }
                        break;
                }

                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (int i = 4 - to.length() % 4;i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }

    private static String getUrl(String areaId, String type) {
        String appId = "3a198aa99bcbc836";
        String appIdSix = "3a198a";
        String privateKey = "f6a3e4_SmartWeatherAPI_af1d233";

        Date date = new Date();
        String currentTime = DateFormat.format("yyyyMMddHHmm", date).toString();
        Log.i("CurrentTime", currentTime);

        String publicKey = "http://open.weather.com.cn/data/?areaid=" + areaId
                + "&type=" + type + "&date=" + currentTime + "&appid=" + appId;

        String key = standardUrlEncoder(publicKey, privateKey);

        String url = "http://open.weather.com.cn/data/?areaid=" + areaId + "&type="
                + type + "&date=" + currentTime + "&appid=" + appIdSix + "&key="
                + key;
        return url;
    }

    public static String getWeather(String areaId, String type) throws IOException {
        String url = getUrl(areaId, type);
        Log.i("URL", url);

        URL httpUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection)httpUrl.openConnection();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String read = null;
        while ((read = reader.readLine()) != null) {
            builder.append(read);
        }
        reader.close();

        return builder.toString();
    }
}
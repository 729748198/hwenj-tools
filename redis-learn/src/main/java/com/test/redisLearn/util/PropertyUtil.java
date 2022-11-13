package com.test.redisLearn.util;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings({"ALL", "AlibabaLowerCamelCaseVariableNaming"})
@Slf4j
public class PropertyUtil {
    private static PropertyUtil reader = new PropertyUtil();

    private Properties props = new Properties();

    public static PropertyUtil getInstance() {
        return reader;
    }

    /**
     * 根据配置文件路径加载配置文件
     *
     * @param path
     * @return
     */
    public static Properties load(String path) {
        Properties properties = new Properties();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("加载配置文件失败:" + path);
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }

    public String read(String key) {
        String value = props.getProperty(key);
        return value;
    }

    /**
     * 根据传输的路径获取配置属性
     *
     * @param filePath
     * @return
     */
    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public Properties getProperties(String filePath) {
        Properties m_properties = new Properties();
        try {
            System.out.println(this.getClass().getClassLoader().getResource("").getPath());

            String basePath = this.getClass().getClassLoader().getResource("/").getPath();
            basePath = basePath.replaceAll("%20", " ");
            String path = basePath + filePath;
            m_properties.load(new FileInputStream(path));
        } catch (Exception ex) {
            ex.printStackTrace();
            m_properties = null;
        }
        return m_properties;
    }
}

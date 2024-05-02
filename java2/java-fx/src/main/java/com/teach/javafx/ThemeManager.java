package com.teach.javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//应用程序主题管理器，单例
public class ThemeManager {
    private static ThemeManager instance;
    private final String CSS_PATH = "css/";
    //储存主题文件的主题名称-文件名映射
    private Map<String, String> styleSheets = new HashMap();
    //储存已加载的主题
    private Map<String, String> loadedTheme = new HashMap<>();
    //当前主题
    private String currentTheme;

    private ThemeManager(){
        URL cssFolderUrl = MainApplication.class.getResource(CSS_PATH);
        if(cssFolderUrl != null){
            File cssFolder = new File(cssFolderUrl.getFile());
            cssFolder.listFiles((dir, name) -> {
                if(name.endsWith(".css")){
                    int pos = name.indexOf("-theme");
                    if(pos < 0){
                        //不是主题文件，退出
                        return false;
                    }
                    //获取主题名称
                    String themeName = name.substring(0, pos);
                    styleSheets.put(themeName, name);
                }
                return false;
            });
        }

        //加载默认主题
        String themeFile = styleSheets.get("default");
        String theme = MainApplication.class.getResource(CSS_PATH + themeFile).toExternalForm();;
        currentTheme = theme;
        loadedTheme.put("default", theme);
    }

    //根据提供的主题名称加载并返回相应的主题
    public String getTheme(String themeName){
        String themeFile = styleSheets.get(themeName);
        if(themeFile == null){
            //提供的主题不存在，直接退出
            return null;
        }
        //先从已加载的主题文件中寻找，若已经加载则直接返回
        String theme = loadedTheme.get(themeName);
        if(theme != null){
            return theme;
        }
        //若未加载，则加载后返回，并将其储存在已加载的主题中
        theme = MainApplication.class.getResource(CSS_PATH + themeFile).toExternalForm();
        loadedTheme.put(themeName, theme);
        return theme;
    }

    private static void initThemeManager(){
        if(instance == null){
            instance = new ThemeManager();
        }
    }

    public static ThemeManager getInstance(){
        return instance;
    }

    public static void init(){
        if(instance != null){
            return;
        }
        initThemeManager();
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String currentTheme) {
        this.currentTheme = currentTheme;
    }
}

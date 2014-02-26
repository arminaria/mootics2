package utils;

/**
 * Created by Armin on 06.02.14.
 */
public class CategoryUtil {
    public static String getCategory(String info) {
        info = info.toLowerCase();
        if(info.contains("homework")){
            return "Homework";
        }else if(info.contains("video")){
            return "Video";
        }else if(info.contains("book") || info.contains("lecture") || info.contains("vorlesungsfolien")){
            return "Lecture";
        }else if(info.contains("forum")){
            return "Forum";
        }else if(info.contains("exercise")){
            return "Exercise";
        }else if(info.contains("self-test")){
            return "Test";
        }else if(info.contains("kahoot")){
            return "Kahoot";
        }else if(info.contains("skript") || info.contains("script")){
            return "Script";
        }else if(info.contains("Exam")){
            return "Exam";
        }else {
            return "others";
        }
    }
}

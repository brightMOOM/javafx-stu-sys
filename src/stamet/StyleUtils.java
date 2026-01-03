package stamet;

import javafx.scene.Parent;

public class StyleUtils {

	public static void useCss(Parent parent, String cssPath) {
        // 1. 校验传入的控件是否为null（第一步防御）
        if (parent == null) {
            System.err.println("[StyleUtils] 错误：传入的JavaFX控件对象为null！");
            return;
        }

        // 2. 先获取资源URL，再判断是否为null（避免调用toExternalForm()空指针）
        java.net.URL cssUrl = parent.getClass().getResource(cssPath);
        if (cssUrl == null) {
            System.err.println("[StyleUtils] 样式文件未找到！请检查路径：" + cssPath);
            // 打印当前类加载路径，方便排查文件位置
            System.err.println("[StyleUtils] 当前类加载根路径：" + parent.getClass().getResource("/"));
            return;
        }

        // 3. 路径有效时，转换为字符串并加载样式
        String fullCssPath = cssUrl.toExternalForm();
        parent.getStylesheets().add(fullCssPath);
    }
}

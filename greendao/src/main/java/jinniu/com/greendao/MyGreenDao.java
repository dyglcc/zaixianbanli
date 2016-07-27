package jinniu.com.greendao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyGreenDao {
    // 号码导入记录
    // 字段1添加日期,添加名称添加数量,添加地区

    public static void main(String[] args) throws Exception {
        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "jiafen.jinniu.com");
        // 当然，如果你愿意，你也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示：
        // Schema schema = new Schema(1, "me.ly.bean");
        // schema.setDefaultJavaPackageDao("me.ly.dao");
        // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();
        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
//        addNote(schema);
        addHistoryHaoduan(schema);
        addHaoduan(schema);
        // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码，此处你需要根据自己的情况更改输出目录（既之前创建的 java-gen)。
        // 其实，输出目录的路径可以在 build.gradle 中设置，有兴趣的朋友可以自行搜索，这里就不再详解。
        new DaoGenerator().generateAll(schema, "/Users/dongyuangui/GITHUB/zaixianbanli/app/src/main/java-gen");
    }

    /**
     * 号段表
     */
    private static void addHaoduan(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Girl」（既类名）
        Entity haoduan_his = schema.addEntity("PhoneNumber");
        // 你也可以重新给表命名
        // note.setTableName("Girls");
        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        haoduan_his.addIdProperty();
        haoduan_his.addLongProperty("his_id").notNull();
        haoduan_his.addIntProperty("phonenum").notNull();
    }

    /**
     * 号段导入历史
     */
    private static void addHistoryHaoduan(Schema schema) {

        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Girl」（既类名）
        Entity haoduan_his = schema.addEntity("Add_history");
        // 你也可以重新给表命名
        // note.setTableName("Girls");
        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        haoduan_his.addIdProperty();
        haoduan_his.addIntProperty("count").notNull();
        haoduan_his.addStringProperty("place").notNull();
        haoduan_his.addLongProperty("time").notNull();
    }

    /**
     *  demo
     */
    private static void addNote(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「Girl」（既类名）
        Entity note = schema.addEntity("Girl");
        // 你也可以重新给表命名
        // note.setTableName("Girls");
        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        note.addStringProperty("iconUrl").notNull();
        note.addStringProperty("mediumUrl").notNull();
        note.addStringProperty("name").notNull();
        note.addIntProperty("height").notNull();
        note.addIntProperty("width").notNull();
    }
}

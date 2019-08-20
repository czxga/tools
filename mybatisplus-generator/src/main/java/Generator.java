/**
 * Created by shiyu.cao on 2017/12/27.
 */

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 代码生成
 *
 * @author ShenHuaJie
 */
public class Generator {
    /**
     * 测试 run 执行
     * <p>
     * 配置方法查看
     * </p>
     */
    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("D:\\icu-master\\icu-core\\src\\main\\java");
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor("Cao");

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }
        });
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("dba_admin");
        dsc.setPassword("Aq1sw2de3");
        dsc.setUrl("jdbc:mysql://103.10.0.117:3306/icu?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(new String[]{
                "med_od_order_exec",
                "med_od_order_handle_rec",
                "med_od_order_info",
                "med_od_type",

                /*"t_deal_order_item",
                /*"t_deal_reservation",
                "t_fina_receipts",
                "t_fina_receipts_item",
                "t_fina_receiv_item",
                "t_fina_receivable",
                "t_fina_settle_detail",
                "t_fina_settlement",
                "t_fina_verification",
                "t_prod_item",
                "t_prod_sku",
                "t_prod_spu",
                "t_prod_subitem",*/
        });
        // 排除生成的表
        // strategy.setExclude(new String[]{"test"});
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //pc.setParent("com.fosun.hcloud.adapter.persistence");
        pc.setParent("com.icu.core.persistence");
        pc.setEntity("model");
        pc.setMapper("mapper");
        pc.setXml("mapper");
        mpg.setPackageInfo(pc);

        // 执行生成
        mpg.execute();
    }
}


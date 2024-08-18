package cn.superhuang.data.scalpel.actuator;

public class Test {
    public static void main(String[] args) {

//
//        String driver = "org.postgresql.Driver";
//        String url = "jdbc:postgresql://10.0.0.172:5432/sde02?stringtype=unspecified";
//        String table = "sys_user";
//        String username = "postgres";
//        String password = "Gistack@123";
//
//        SparkSession sparkSession = SparkSession.builder().master("local").getOrCreate();
//        Dataset<Row> dataset = sparkSession.read()
//                .format("jdbc")
//                .option("driver", driver)
//                .option("url", url)
//                .option("dbtable", "\"ATT_CWS_BASE\"")
//                .option("user", username)
//                .option("password", password)
//                .load();
//
//        dataset.show();
//        VerificationSuite verificationSuite = new VerificationSuite();
//        VerificationResult verificationResult = verificationSuite.onData(dataset)
//                .addCheck(
//                        new Check(CheckLevel.Error(), "unit test my data",
//                                JavaConverters.asScalaIteratorConverter(new ArrayList<Constraint>().iterator())
//                                        .asScala().toSeq())
//                                .isComplete("CWS_CODE", Option.empty()))
//                .run();
//
//        System.out.println(verificationResult);
    }
}
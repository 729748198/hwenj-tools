import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import com.alibaba.fastjson.JSON;
import com.hwenj.excel.bean.CluesAptIssued;
import com.hwenj.excel.bean.CluesBlackIssued;
import com.hwenj.excel.bean.CluesCount;
import com.hwenj.excel.bean.Information;
import com.hwenj.excel.bean.IntelligenceFinalRaw;
import com.hwenj.excel.bean.IntelligenceManufacturersLog;
import com.hwenj.excel.bean.OutLog;
import com.hwenj.excel.util.CsvWriterUtil;
import com.hwenj.excel.util.DateUtil;
import org.assertj.core.util.Lists;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author hwenj
 * @since 2022/10/2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestReadExcel.class)
public class TestReadExcel {
    // 可优化数据结构
    HashMap<String, List<CluesAptIssued>> aptMap = new HashMap<>();
    HashMap<String, List<CluesBlackIssued>> blackMap = new HashMap<>();
    // 这个方法内只添加apt，统计次数，输出文件
    Set<CluesAptIssued> cluesAptIssuedSet = new HashSet<>();
    // 这个方法内只添加black，统计次数，输出文件
    Set<CluesBlackIssued> cluesBlackIssuedSet = new HashSet<>();

    // 厂商线索数据生成记录
    List<IntelligenceManufacturersLog> manufacturersLogs = new ArrayList<>();

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String date = df.format(new Date());

    @Test
    public void testReadExcel() {
        String logReadPath = "D:/home/read/";
        File[] companys = FileUtil.ls(logReadPath);

        Information information = getInformation();
    }

    private Information getInformation() {
        // csv文件读取器
        final CsvReader reader = CsvUtil.getReader();
        String logInfoPath = "D:/home/info/";
        // 获取基础信息  information
        Information information = new Information();

        List<File> infos = FileUtil.loopFiles(logInfoPath + File.separator + "/");
        for (File info : infos) {
            if (info.getName().startsWith("information")) {
                // 若使用Utf8 可以直接使用ResourceUtil.getUtf8Reader("test2.csv")
                List<Information> rows = reader.read(ResourceUtil.getUtf8Reader(info.getPath()), Information.class);
                information = rows.get(0);
            }
        }
        return information;
    }

    @Test
    public void listReadFile() {
        // info文件
        Information information = getInformation();
        // 最终分析文件
        List<CluesCount> CluesCounts = new ArrayList<>();

        List<File> files = FileUtil.loopFiles("D:/home/read/" + File.separator + "/");
        int filterMatch = 0;
        int filterSrcIp = 0;
        Map<String, String> resultMap = new HashMap<>();
        final CsvReader reader = CsvUtil.getReader();
        for (File file : files) {
            // 注释前的代码
            if (file.getPath().contains("outgoing")) {
                final List<OutLog> logs = reader.read(ResourceUtil.getUtf8Reader(file.getPath()), OutLog.class);
                for (OutLog outLog : logs) {
                    if ((outLog.getSrcIp() != null) && (!outLog.getSrcIp().isEmpty())) {
                        if (!Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", outLog.getSrcIp())) {
                            filterMatch++;
                            continue;
                        }
                    } else {
                        filterSrcIp++;
                        continue;
                    }
                    // todo,不是简单的build,需要过滤重复的，更新时间
                    resultMap = buildOutLog(outLog);
                }
            }
            // 注释后的代码
            // 情报匹配查询

            // 情况统计，用来最后生成记录
            CluesCount cluesCount = new CluesCount();
            cluesCount.setProvince(information.getProvince())
                    .setIndustry(information.getIndustry())
                    .setDeploymentLocation(information.getDeploymentLocation())
                    .setCompany("company.getCompany()")
                    .setSpecialNum("companyNum");


            int size = resultMap.size();
            Set<Map.Entry<String, String>> entries = resultMap.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            buildIterator(iterator, information, size);

            // 厂商数据生成
            iteratorAptSet();
            iteratorBlackSet();

            // 输出apt
            Integer aptNumb = getAptNum();
            outAptFile();
            // 输出black
            Integer blackNum = getBlackNum();
            outBlackFile();

            // 线索情况统计文件
            cluesCount.setIocNum(aptNumb + blackNum).setAptNum(aptNumb).setBlackNum(blackNum).setSn(CluesCounts.size() + 1);
            CluesCounts.add(cluesCount);
            File cluesCountsCsvFile1 = FileUtil.newFile("D:/home/sldata2022_926/write" + "/" + date + "/" + "CluesCounts.csv");
            CsvWriter cluesCountsCsvWriter1 = CsvUtil.getWriter(cluesCountsCsvFile1, Charset.forName("GBK"), true);
            CsvWriterUtil.writeBeans(cluesCountsCsvWriter1, CluesCounts, FileUtil.isEmpty(cluesCountsCsvFile1));
            cluesCountsCsvWriter1.flush();
            cluesCountsCsvWriter1.close();
        }
    }

    private void outBlackFile() {
        HashMap<String, CluesBlackIssued> cluesBlackIssuedMap = buildBlackMap();
        List<CluesBlackIssued> collect1 = new ArrayList<>();
        Set<Map.Entry<String, CluesBlackIssued>> blackSet1 = cluesBlackIssuedMap.entrySet();
        Iterator<Map.Entry<String, CluesBlackIssued>> iterator1 = blackSet1.iterator();
        while (iterator1.hasNext()) {
            Map.Entry<String, CluesBlackIssued> next = iterator1.next();
            CluesBlackIssued Value = next.getValue();
            Value.setSn(collect1.size() + 1);
            collect1.add(Value);
        }


        // 全量数据
        File blackCsvFile1 = FileUtil.newFile("D:/home/sldata2022_926/write/" + "/" + date + "/all/" + "company.getCompany()" + "/" + " company.getCompany()" + "-black-all-data.csv");
        CsvWriter blackCsvWriter1 = CsvUtil.getWriter(blackCsvFile1, Charset.forName("GBK"), true);
        CsvWriterUtil.writeBeans(blackCsvWriter1, collect1, FileUtil.isEmpty(blackCsvFile1));
        blackCsvWriter1.flush();
        blackCsvWriter1.close();
        System.out.println("{}，黑客攻击，厂商全量数据文件数据已生成");
        collect1.clear();
        cluesBlackIssuedMap.clear();
    }

    private HashMap<String, CluesBlackIssued> buildBlackMap() {
        HashMap<String, CluesBlackIssued> cluesBlackIssuedMap = new HashMap<>();
        HashMap<String, Set<String>> cluesBlackIssuedVendorMap = new HashMap<>();


        for (CluesBlackIssued blackIssued : cluesBlackIssuedSet) {
            if (cluesBlackIssuedVendorMap.containsKey(blackIssued.getIoc())) {
                Set<String> vendor = cluesBlackIssuedVendorMap.get(blackIssued.getIoc());
                vendor.add(blackIssued.getVendor());
                cluesBlackIssuedVendorMap.put(blackIssued.getIoc(), vendor);
            } else {
                Set<String> set = new HashSet<>();
                set.add(blackIssued.getVendor());
                cluesBlackIssuedVendorMap.put(blackIssued.getIoc(), set);
            }

        }

        Integer blackNum = cluesBlackIssuedVendorMap.size();

        for (CluesBlackIssued cluesBlackIssued : cluesBlackIssuedSet) {
            String key = cluesBlackIssued.getSrcHost() + "#" + cluesBlackIssued.getIoc();
            if (cluesBlackIssuedMap.containsKey(key)) {
                CluesBlackIssued blackIssued = cluesBlackIssuedMap.get(key);

                if (StringUtils.hasText(cluesBlackIssued.getPlatform())) {
                    Set<String> platforms = new HashSet<>();
                    Arrays.stream(blackIssued.getPlatform().split(",")).map(platforms::add);
                    platforms.add(cluesBlackIssued.getPlatform());
                    blackIssued.setPlatform(String.join(",", platforms));
                }

                if (StringUtils.hasText(cluesBlackIssued.getDescription())) {
                    Set<String> descriptions = new HashSet<>();
                    Arrays.stream(blackIssued.getDescription().split(",")).map(descriptions::add);
                    descriptions.add(cluesBlackIssued.getDescription());
                    blackIssued.setDescription(String.join(",", descriptions));
                }

                if (StringUtils.hasText(cluesBlackIssued.getIocOrig())) {
                    Set<String> iocOrigs = new HashSet<>();
                    Arrays.stream(blackIssued.getIocOrig().split(",")).map(iocOrigs::add);
                    iocOrigs.add(cluesBlackIssued.getIocOrig());
                    blackIssued.setIocOrig(String.join(",", iocOrigs));
                }

                if (StringUtils.hasText(cluesBlackIssued.getSamples())) {
                    Set<String> samples = new HashSet<>();
                    Arrays.stream(blackIssued.getSamples().split(",")).map(samples::add);
                    samples.add(cluesBlackIssued.getSamples());
                    blackIssued.setSamples(String.join(",", samples));
                }

                if (StringUtils.hasText(cluesBlackIssued.getAttackIntention())) {
                    Set<String> attackIntentions = new HashSet<>();
                    Arrays.stream(blackIssued.getAttackIntention().split(",")).map(attackIntentions::add);
                    attackIntentions.add(cluesBlackIssued.getAttackIntention());
                    blackIssued.setAttackIntention(String.join(",", attackIntentions));
                }

                if (StringUtils.hasText(cluesBlackIssued.getFamily())) {
                    Set<String> familys = new HashSet<>();
                    Arrays.stream(blackIssued.getFamily().split(",")).map(familys::add);
                    familys.add(cluesBlackIssued.getFamily());
                    blackIssued.setFamily(String.join(",", familys));
                }


                Set<String> set = cluesBlackIssuedVendorMap.get(cluesBlackIssued.getIoc());
                blackIssued.setVendor(String.join(",", set));

                blackIssued.setSn(cluesBlackIssuedMap.size() + 1);
                cluesBlackIssuedMap.put(key, blackIssued);
            } else {
                cluesBlackIssued.setSn(1);
                cluesBlackIssuedMap.put(key, cluesBlackIssued);
            }
        }
        return cluesBlackIssuedMap;
    }


    private void outAptFile() {
        HashMap<String, CluesAptIssued> cluesAptIssuedMap = buildCluesAptIssuedMap();

        List<CluesAptIssued> aptCollect1 = new ArrayList<>();
        Set<Map.Entry<String, CluesAptIssued>> aptSet1 = cluesAptIssuedMap.entrySet();
        Iterator<Map.Entry<String, CluesAptIssued>> aptIterator1 = aptSet1.iterator();
        while (aptIterator1.hasNext()) {
            Map.Entry<String, CluesAptIssued> next = aptIterator1.next();
            CluesAptIssued Value = next.getValue();
            Value.setSn(aptCollect1.size() + 1);
            aptCollect1.add(Value);
        }

        File aptCsvFile1 = FileUtil.newFile("D:/home/sldata2022_926/write/" + "/" + date + "/all/" + "/" + "company.getCompany()" + "company.getCompany()-apt-all-data.csv");
        CsvWriter aptCsvWriter1 = CsvUtil.getWriter(aptCsvFile1, Charset.forName("GBK"), true);
        CsvWriterUtil.writeBeans(aptCsvWriter1, aptCollect1, FileUtil.isEmpty(aptCsvFile1));
        aptCsvWriter1.flush();
        aptCsvWriter1.close();
        System.out.println("{}，APT，厂商全量数据文件数据已生成");

        aptCollect1.clear();
        cluesAptIssuedMap.clear();
    }

    /**
     * 生成APT情报线索Map
     *
     * @return
     */
    private HashMap<String, CluesAptIssued> buildCluesAptIssuedMap() {
        HashMap<String, CluesAptIssued> cluesAptIssuedMap = new HashMap<>();
        HashMap<String, Set<String>> cluesAptIssuedVendorMap = new HashMap<>();
        for (CluesAptIssued cluesAptIssued : cluesAptIssuedSet) {
            if (cluesAptIssuedVendorMap.containsKey(cluesAptIssued.getIoc())) {
                Set<String> aptVendor = cluesAptIssuedVendorMap.get(cluesAptIssued.getIoc());
                aptVendor.add(cluesAptIssued.getVendor());
                cluesAptIssuedVendorMap.put(cluesAptIssued.getIoc(), aptVendor);
            } else {
                Set<String> set = new HashSet<>();
                set.add(cluesAptIssued.getVendor());
                cluesAptIssuedVendorMap.put(cluesAptIssued.getIoc(), set);
            }
        }
        // 全量数据
        for (CluesAptIssued cluesAptIssued : cluesAptIssuedSet) {
            String key = cluesAptIssued.getSrcHost() + "#" + cluesAptIssued.getIoc();
            if (cluesAptIssuedMap.containsKey(key)) {
                CluesAptIssued aptIssued = cluesAptIssuedMap.get(key);
                if (StringUtils.hasText(cluesAptIssued.getPlatform())) {
                    Set<String> platforms = new HashSet<>();
                    Arrays.stream(aptIssued.getPlatform().split(",")).map(platforms::add);
                    platforms.add(cluesAptIssued.getPlatform());
                    aptIssued.setPlatform(String.join(",", platforms));
                }

                if (StringUtils.hasText(cluesAptIssued.getDescription())) {
                    Set<String> descriptions = new HashSet<>();
                    Arrays.stream(aptIssued.getDescription().split(",")).map(descriptions::add);
                    descriptions.add(cluesAptIssued.getDescription());
                    aptIssued.setDescription(String.join(",", descriptions));
                }

                if (StringUtils.hasText(cluesAptIssued.getIocOrig())) {
                    Set<String> iocOrigs = new HashSet<>();
                    Arrays.stream(aptIssued.getIocOrig().split(",")).map(iocOrigs::add);
                    iocOrigs.add(cluesAptIssued.getIocOrig());
                    aptIssued.setIocOrig(String.join(",", iocOrigs));
                }

                if (StringUtils.hasText(cluesAptIssued.getSamples())) {
                    Set<String> samples = new HashSet<>();
                    Arrays.stream(aptIssued.getSamples().split(",")).map(samples::add);
                    samples.add(cluesAptIssued.getSamples());
                    aptIssued.setSamples(String.join(",", samples));
                }

                if (StringUtils.hasText(cluesAptIssued.getAttackIntention())) {
                    Set<String> attackIntentions = new HashSet<>();
                    Arrays.stream(aptIssued.getAttackIntention().split(",")).map(attackIntentions::add);
                    attackIntentions.add(cluesAptIssued.getAttackIntention());
                    aptIssued.setAttackIntention(String.join(",", attackIntentions));
                }

                if (StringUtils.hasText(cluesAptIssued.getIocLink())) {
                    Set<String> iocLinks = new HashSet<>();
                    Arrays.stream(aptIssued.getIocLink().split(",")).map(iocLinks::add);
                    iocLinks.add(cluesAptIssued.getIocLink());
                    aptIssued.setIocLink(String.join(",", iocLinks));
                }

                if (StringUtils.hasText(cluesAptIssued.getFamily())) {
                    Set<String> familys = new HashSet<>();
                    Arrays.stream(aptIssued.getFamily().split(",")).map(familys::add);
                    familys.add(cluesAptIssued.getFamily());
                    aptIssued.setFamily(String.join(",", familys));
                }


                Set<String> set = cluesAptIssuedVendorMap.get(cluesAptIssued.getIoc());
                aptIssued.setVendor(String.join(",", set));

                cluesAptIssued.setSn(cluesAptIssuedMap.size() + 1);
                cluesAptIssuedMap.put(key, aptIssued);
            } else {
                cluesAptIssued.setSn(1);
                cluesAptIssuedMap.put(key, cluesAptIssued);
            }
        }

        return cluesAptIssuedMap;
    }

    /**
     * 统计AptCount
     */
    private Integer getAptNum() {
        HashMap<String, Set<String>> cluesAptIssuedVendorMap = new HashMap<>();
        for (CluesAptIssued cluesAptIssued : cluesAptIssuedSet) {
            if (cluesAptIssuedVendorMap.containsKey(cluesAptIssued.getIoc())) {
                Set<String> aptVendor = cluesAptIssuedVendorMap.get(cluesAptIssued.getIoc());
                aptVendor.add(cluesAptIssued.getVendor());
                cluesAptIssuedVendorMap.put(cluesAptIssued.getIoc(), aptVendor);
            } else {
                Set<String> set = new HashSet<>();
                set.add(cluesAptIssued.getVendor());
                cluesAptIssuedVendorMap.put(cluesAptIssued.getIoc(), set);
            }
        }
        return cluesAptIssuedVendorMap.size();
    }

    private Integer getBlackNum() {
        HashMap<String, Set<String>> cluesBlackIssuedVendorMap = new HashMap<>();
        for (CluesBlackIssued blackIssued : cluesBlackIssuedSet) {
            if (cluesBlackIssuedVendorMap.containsKey(blackIssued.getIoc())) {
                Set<String> vendor = cluesBlackIssuedVendorMap.get(blackIssued.getIoc());
                vendor.add(blackIssued.getVendor());
                cluesBlackIssuedVendorMap.put(blackIssued.getIoc(), vendor);
            } else {
                Set<String> set = new HashSet<>();
                set.add(blackIssued.getVendor());
                cluesBlackIssuedVendorMap.put(blackIssued.getIoc(), set);
            }

        }

        return cluesBlackIssuedVendorMap.size();
    }


    /**
     * 遍历aptMap
     */
    private void iteratorAptSet() {

        Set<Map.Entry<String, List<CluesAptIssued>>> aptSet = aptMap.entrySet();
        Iterator<Map.Entry<String, List<CluesAptIssued>>> aptEntryIterator = aptSet.iterator();
        while (aptEntryIterator.hasNext()) {
            Map.Entry<String, List<CluesAptIssued>> next = aptEntryIterator.next();
            String key = next.getKey();
            List<CluesAptIssued> value = next.getValue();

            //String supplierName = commonService.getSupplierNumberByName(key);
            String supplierName = "commonService.getSupplierNumberByName(key)";
            File dataCsvFile = FileUtil.newFile("D:/home/sldata2022_926/vendor" + "/" + date + "/" + "vendor/" + key + "/" + "company.getCompanyYsNum()" + "-apt-data.csv");
            CsvWriter dataCsvWriter = CsvUtil.getWriter(dataCsvFile, Charset.forName("GBK"), true);
            CsvWriterUtil.writeBeans(dataCsvWriter, value, FileUtil.isEmpty(dataCsvFile));
            dataCsvWriter.flush();
            dataCsvWriter.close();
            System.out.println("{}，APT，{}厂商数据文件数据已生成" + "company.getCompany()" + key);
            // 记录检查单位需要进行研判的厂商记录
            IntelligenceManufacturersLog log = new IntelligenceManufacturersLog();
            log.setCompany("company.getCompany()");
            log.setCompanyNo("company.getCompanyYsNum()");
            log.setSupplier(key);
            log.setSupplierNo(supplierName);
            log.setLogType(1);
            log.setCluesType(1);
            log.setCluesFilePath(dataCsvFile.getAbsolutePath());
            manufacturersLogs.add(log);
        }
        aptMap.clear();
    }

    /**
     * 遍历blackMap
     *
     * @return
     */
    private void iteratorBlackSet() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(new Date());
        Set<Map.Entry<String, List<CluesBlackIssued>>> blackSet = blackMap.entrySet();
        Iterator<Map.Entry<String, List<CluesBlackIssued>>> blackEntryIterator = blackSet.iterator();
        while (blackEntryIterator.hasNext()) {
            Map.Entry<String, List<CluesBlackIssued>> next = blackEntryIterator.next();
            String key = next.getKey();
            List<CluesBlackIssued> value = next.getValue();

            String supplierName = "commonService.getSupplierNumberByName(key)";
            File dataCsvFile = FileUtil.newFile("D:/home/sldata2022_926/vendor" + "/" + date + "/" + "vendor" + "/" + key + "/" + "company.getCompanyYsNum()" + "-black-data.csv");
            CsvWriter dataCsvWriter = CsvUtil.getWriter(dataCsvFile, Charset.forName("GBK"), true);
            CsvWriterUtil.writeBeans(dataCsvWriter, value, FileUtil.isEmpty(dataCsvFile));
            dataCsvWriter.flush();
            dataCsvWriter.close();
            System.out.println("{}，APT，{}厂商数据文件数据已生成" + "company.getCompany()" + key);
            // 记录检查单位需要进行研判的厂商记录
            IntelligenceManufacturersLog log = new IntelligenceManufacturersLog();
            log.setCompany("company.getCompany()");
            log.setCompanyNo("company.getCompanyYsNum()");
            log.setSupplier(key);
            log.setSupplierNo(supplierName);
            log.setLogType(1);
            log.setCluesType(2);
            log.setCluesFilePath(dataCsvFile.getAbsolutePath());
            manufacturersLogs.add(log);
        }
        blackMap.clear();
    }


    private Map<String, String> buildOutLog(OutLog outLog) {
        final Map<String, String> resultMap = new HashMap<>();
        String externalIoc = "";

        // 自定义外联情报字段
        if ("IP情报".equals(outLog.getHitType())) {
            externalIoc = outLog.getDestIp();
        } else if ("域名情报".equals(outLog.getHitType())) {
            externalIoc = outLog.getDestAddress();
        } else if ("URL情报".equals(outLog.getHitType())) {
            externalIoc = outLog.getDestAddress();
        }
        outLog.setExternalIoc(externalIoc);
        outLog.setCompany("company.getCompanyYsNum()");
        buildHitMark(resultMap, outLog, externalIoc);
        return resultMap;


    }

    /**
     * 以受控设备+情报类型为key，设置到map中
     * 114.242.143.169 受控设备IP
     * <p>
     * 此方法主要设置开始攻击时间，结束攻击时间
     * 统计源端口聚合，目的端口聚合，命中的情报厂商聚合 ，并且按|符合分割
     *
     * @param resultMap
     * @param outLog
     * @param externalIoc 情报类型-IP情报，域名情报，URL情报
     */
    public void buildHitMark(Map<String, String> resultMap, OutLog outLog, String externalIoc) {
        /**
         * 设置时间格式
         */
        String firstExternalTime = null;
        String endExternalTime = null;
        if (outLog.getTime().contains("_")) {
            firstExternalTime = outLog.getTime().replaceAll("_", "-");
            endExternalTime = outLog.getTime().replaceAll("_", "-");
        } else {
            firstExternalTime = outLog.getTime();
            endExternalTime = outLog.getTime();
        }

        LocalDateTime firstLocalDateTime = LocalDateTime.parse(firstExternalTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Date firstDate = Date.from(firstLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime endLocalDateTime = LocalDateTime.parse(endExternalTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());


        if ("情报命中".equals(outLog.getHitRemark())) {
            String key = outLog.getSrcIp() + "#" + externalIoc;
            if (resultMap.containsKey(key)) {

                String jsonStr = resultMap.get(key);
                OutLog outLogS = JSON.parseObject(jsonStr, OutLog.class);

                outLog.setExternalCount(outLogS.getExternalCount() + 1);

                Date firstExternalTimeS = outLogS.getFirstExternalTime();
                Date endExternalTimeS = outLogS.getEndExternalTime();

                LocalDateTime firstLocalDateTimeS = LocalDateTime.parse(DateUtil.dateToStr(firstExternalTimeS, "yyyy-MM-dd HH:mm:ss"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime endLocalDateTimeS = LocalDateTime.parse(DateUtil.dateToStr(endExternalTimeS, "yyyy-MM-dd HH:mm:ss"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // 开始攻击时间聚合
                boolean before = firstLocalDateTime.isBefore(firstLocalDateTimeS);
                outLog.setFirstExternalTime(before ? firstDate : firstExternalTimeS);
                // 结束攻击时间聚合
                boolean after = endLocalDateTime.isAfter(endLocalDateTimeS);
                outLog.setEndExternalTime(after ? endDate : endExternalTimeS);


                // 源端口聚合
                if (outLog.getSrcPort() != null && outLogS.getSrcPort().length() < 1000) {
                    Set<String> collect = Arrays.stream(outLogS.getSrcPort().split("\\|")).collect(Collectors.toSet());

                    collect.add(outLog.getSrcPort());
                    outLog.setSrcPort(String.join("|", collect));

                }
                // 目的端口聚合
                if (outLog.getDestPort() != null && outLogS.getDestPort().length() < 1000) {
                    Set<String> collect = Arrays.stream(outLogS.getDestPort().split("\\|")).collect(Collectors.toSet());
                    collect.add(outLog.getDestPort());
                    outLog.setDestPort(String.join("|", collect));
                }
                // 命中的情报厂商聚合
                if (outLog.getSourceNums() != null) {
                    Set<String> collect = Arrays.stream(outLogS.getSourceNums().split("\\|")).collect(Collectors.toSet());
                    Set<String> collect2 = Arrays.stream(outLog.getSourceNums().split("\\|")).collect(Collectors.toSet());
                    collect.addAll(collect2);
                    outLog.setSourceNums(String.join("|", collect));
                }


            } else {
                outLog.setExternalCount(outLog.getExternalCount() + 1);
                outLog.setFirstExternalTime(firstDate);
                outLog.setEndExternalTime(endDate);
            }
            resultMap.put(key, JSON.toJSONString(outLog));
        }

    }

    /**
     * 遍历es，从intelligence_final_raw_0725中查询每个情报的命中信息
     *
     * @param iterator
     */
    public void buildIterator(Iterator<Map.Entry<String, String>> iterator, Information information, Integer size) {
        final String index = "intelligence_final_raw_0725";
        final String index1 = "export_internal_info_2022-09-26";
        // 匹配的情报LIST，循环遍历用
        List<IntelligenceFinalRaw> intelligenceFinalRaws = new ArrayList<>();
        // 黑客攻击Map，目前只做过滤用
        Map<String, IntelligenceFinalRaw> blackIoc = new HashMap<>();
        // 只做id统计，浪费空间
        List<CluesBlackIssued> blackList = new ArrayList<>();
        // 只做id统计，浪费空间
        List<CluesAptIssued> aptList = new ArrayList<>();
        // todo 这两个可能有问题，过滤了重要的东西
        HashSet<String> vendorAptSet = new HashSet<>();
        HashSet<String> vendorBlackSet = new HashSet<>();

        while (iterator.hasNext()) {

            Map.Entry<String, String> next = iterator.next();
            String value = next.getValue();
            OutLog outLog = JSON.parseObject(value, OutLog.class);
            String externalIoc = "";

            // 自定义外联情报字段
            if ("IP情报".equals(outLog.getHitType())) {
                externalIoc = outLog.getDestIp();
            } else if ("域名情报".equals(outLog.getHitType())) {
                externalIoc = outLog.getDestAddress();
            } else if ("URL情报".equals(outLog.getHitType())) {
                externalIoc = outLog.getDestAddress();
            }
            if (externalIoc == null || externalIoc.isEmpty()) {
                continue;
            }

            /**
             * 根据ioc查询ES
             */
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("ioc.keyword", externalIoc));

            sourceBuilder.query(boolQueryBuilder);
            searchRequest.indices("index").source(sourceBuilder);

//            SearchResponse search = restHighLevelClient231.search(searchRequest);
            SearchResponse search = null;

//            if (search.getHits().getTotalHits() == 0){
//                SearchRequest searchRequest1 = new SearchRequest();
//                searchRequest1.indices(index1).source(sourceBuilder);
//                search = restHighLevelClient231.search(searchRequest1);
//            }


            /**
             * 遍历匹配后的情报
             *
             */
            for (SearchHit hit : search.getHits().getHits()) {

                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                if (sourceAsMap == null) {
                    continue;
                }
                IntelligenceFinalRaw intelligenceFinalRaw = buildIntelligenceFinalRaw(sourceAsMap);

                // es中的字段——ioc_direction
                if ("黑客攻击".equals(intelligenceFinalRaw.getIoc_direction())) {
                    blackIoc.put(intelligenceFinalRaw.getIoc(), intelligenceFinalRaw);
                }

                intelligenceFinalRaws.add(intelligenceFinalRaw);
            }


            for (IntelligenceFinalRaw intelligenceFinalRaw : intelligenceFinalRaws) {

                /**
                 * 校验ipcType和port
                 */
                Boolean checkStatus = checkPort(intelligenceFinalRaw, outLog);

                if (intelligenceFinalRaw.getIoc_direction() != null) {

                    if ("APT".equals(intelligenceFinalRaw.getIoc_direction())) {
                        if (blackIoc.containsKey(intelligenceFinalRaw.getIoc())) {
                            continue;
                        }
                        CluesAptIssued apt = getAptObject(intelligenceFinalRaw, aptList, information, outLog);


                        if (checkStatus) {
                            // TODO 因为厂商前期情报质量差，目前下发厂商是按照全部进行下发研判，项目后期看情况只下发中低置信度的
//                                    if ("高".equals(apt.getConfidence())){
//                                        aptList.add(apt);
//                                    }else {
                            if (vendorAptSet.contains(apt.getVendor() + apt.getSrcHost() + apt.getIoc())) {
                                continue;
                            }

                            CluesAptIssued cluesAptIssued = new CluesAptIssued();
                            BeanUtils.copyProperties(apt, cluesAptIssued);
                            cluesAptIssued.setSn(cluesAptIssuedSet.size() + 1);
                            cluesAptIssuedSet.add(cluesAptIssued);

                            // TODO 线索数据发送至kafka,转储至ES
//                                    this.cluesSendKafka(jacksonUtil.toJson(apt));

                            // TODO 下发厂商的内网IP加密处理
                            apt.setSrcHost("HashidsUtil.ipEncode(apt.getSrcHost()");
                            apt.setCompany("company.getCompanyYsNum()");
                            // TOdo 优化数据结构,添加同一个key时变为list
                            if (aptMap.containsKey(apt.getVendor())) {
                                List<CluesAptIssued> cluesAptIssueds = new ArrayList<>(aptMap.get(apt.getVendor()));
                                apt.setSn(cluesAptIssueds.size() + 1);
                                cluesAptIssueds.add(apt);
                                aptMap.put(apt.getVendor(), cluesAptIssueds);
                                vendorAptSet.add(apt.getVendor() + cluesAptIssued.getSrcHost() + apt.getIoc());
                            } else {
                                apt.setSn(1);
                                aptMap.put(apt.getVendor(), Lists.newArrayList(apt));
                                vendorAptSet.add(apt.getVendor() + cluesAptIssued.getSrcHost() + apt.getIoc());
                            }

//                                    }
                        }

                        // todo 是否和上一个循环重复判断了
                    } else if ("黑客攻击".equals(intelligenceFinalRaw.getIoc_direction())) {
                        CluesBlackIssued black = buildBlack(blackList, information, intelligenceFinalRaw, outLog);

                        if (checkStatus) {
//                                    if("高".equals(black.getConfidence())){
//                                        blackList.add(black);
//                                    }else {
                            if (vendorBlackSet.contains(black.getVendor() + black.getSrcHost() + black.getIoc())) {
                                continue;
                            }
                            CluesBlackIssued cluesBlackIssued = new CluesBlackIssued();
                            BeanUtils.copyProperties(black, cluesBlackIssued);
                            cluesBlackIssued.setSn(cluesBlackIssuedSet.size() + 1);
                            cluesBlackIssuedSet.add(cluesBlackIssued);


                            // TODO 线索数据发送至kafka,转储至ES
//                                    this.cluesSendKafka(jacksonUtil.toJson(black));
                            // IP加密
                            black.setSrcHost("HashidsUtil.ipEncode(black.getSrcHost())");
                            black.setCompany("company.getCompanyYsNum()");
                            if (blackMap.containsKey(black.getVendor())) {
                                List<CluesBlackIssued> cluesBlackIssueds = new ArrayList<>(blackMap.get(black.getVendor()));
                                black.setSn(cluesBlackIssueds.size() + 1);
                                cluesBlackIssueds.add(black);
                                blackMap.put(black.getVendor(), cluesBlackIssueds);
                                vendorBlackSet.add(black.getVendor() + cluesBlackIssued.getSrcHost() + black.getIoc());
                            } else {
                                black.setSn(1);
                                blackMap.put(black.getVendor(), Lists.newArrayList(black));
                                vendorBlackSet.add(black.getVendor() + cluesBlackIssued.getSrcHost() + black.getIoc());
                            }
//                                    }
                        }

                    }
                }
            }

            System.out.println("待处理日志数量：{}" + size--);
        }
    }

    public CluesBlackIssued buildBlack(List<CluesBlackIssued> blackList,
                                       Information information,
                                       IntelligenceFinalRaw intelligenceFinalRaw,
                                       OutLog outLog) {
        return new CluesBlackIssued()
                .setSn(blackList.size() + 1)
                .setProvince(information.getProvince())
                .setIndustry(information.getIndustry())
                .setDeploymentLocation(information.getDeploymentLocation())
                .setCompany("company.getCompany()")
                .setFirstCategory(intelligenceFinalRaw.getCategory_final())
                .setSecondCategory(intelligenceFinalRaw.getType())
                .setSrcHost(outLog.getSrcIp())
                .setSrcPort(outLog.getSrcPort())
                .setIoc(intelligenceFinalRaw.getIoc())
                .setDestPort(outLog.getDestPort())
                .setCountry(intelligenceFinalRaw.getIp_country())
                .setRegion(intelligenceFinalRaw.getIp_city())
                .setPlatform(intelligenceFinalRaw.getPlatform())
                .setDescription(intelligenceFinalRaw.getDescription())
                .setRiskLevel(intelligenceFinalRaw.getRisk_level())
                .setConfidence(intelligenceFinalRaw.getConfidence())
                .setStatusSurvival(intelligenceFinalRaw.getStatus())
                .setCreateTime(intelligenceFinalRaw.getCtime())
                .setUpdateTime(intelligenceFinalRaw.getUtime())
                .setExpireTime(intelligenceFinalRaw.getEtime())
                .setIocOrig(intelligenceFinalRaw.getIoc_org())
                .setSampleAttribute(intelligenceFinalRaw.getIoc_tag())
                .setSamples(intelligenceFinalRaw.getIoc_fileHash())
                .setAttackIntention(intelligenceFinalRaw.getIoc_target())
                .setFamily(intelligenceFinalRaw.getFamily())
                .setExternalCount(outLog.getExternalCount())
                .setFirstExternalTime(DateUtil.dateToStr(outLog.getFirstExternalTime(), "yyyy-MM-dd HH:mm:ss"))
                .setEndExternalTime(DateUtil.dateToStr(outLog.getEndExternalTime(), "yyyy-MM-dd HH:mm:ss"))
                .setVendor(intelligenceFinalRaw.getIoc_orig())
                .setIoc_type(intelligenceFinalRaw.getIoc_type());
    }

    public IntelligenceFinalRaw buildIntelligenceFinalRaw(Map<String, Object> sourceAsMap) {
        String apt_org_agg = "";
        if (sourceAsMap.get("apt_org_agg") != null) {
            apt_org_agg = sourceAsMap.get("apt_org_agg").toString();
        } else {
            if (sourceAsMap.get("apt_org_notfound") != null) {
                apt_org_agg = sourceAsMap.get("apt_org_notfound").toString();
            }
        }
        IntelligenceFinalRaw intelligenceFinalRaw = new IntelligenceFinalRaw();
        intelligenceFinalRaw.setIoc_direction(sourceAsMap.get("ioc_direction") == null ? "" : sourceAsMap.get("ioc_direction").toString())
                .setIoc(sourceAsMap.get("ioc") == null ? "" : sourceAsMap.get("ioc").toString())
                .setIoc_industry(sourceAsMap.get("ioc_industry") == null ? "" : sourceAsMap.get("ioc_industry").toString())
                .setIp_city(sourceAsMap.get("ip_city") == null ? "" : sourceAsMap.get("ip_city").toString())
                .setConfidence(sourceAsMap.get("confidence") == null ? "" : sourceAsMap.get("confidence").toString())
                .setFamily(sourceAsMap.get("family_agg") == null ? "" : sourceAsMap.get("family_agg").toString())
                .setApt_country(sourceAsMap.get("apt_country") == null ? "" : sourceAsMap.get("apt_country").toString())
                .setIp_country(sourceAsMap.get("ip_country") == null ? "" : sourceAsMap.get("ip_country").toString())
                .setDescription(sourceAsMap.get("description") == null ? "" : sourceAsMap.get("description").toString())
                .setRisk_level(sourceAsMap.get("risk_level") == null ? "" : sourceAsMap.get("risk_level").toString())
                .setStatus(sourceAsMap.get("status") == null ? "" : sourceAsMap.get("status").toString())
                .setCtime(sourceAsMap.get("ctime") == null ? "" : sourceAsMap.get("ctime").toString())
                .setEtime(sourceAsMap.get("etime") == null ? "" : sourceAsMap.get("etime").toString())
                .setUtime(sourceAsMap.get("utime") == null ? "" : sourceAsMap.get("utime").toString())
                .setIoc_org(sourceAsMap.get("ioc_org") == null ? "" : sourceAsMap.get("ioc_org").toString())
                .setIoc_fileHash(sourceAsMap.get("fileHash_agg") == null ? "" : sourceAsMap.get("fileHash_agg").toString())
                .setIoc_target(sourceAsMap.get("ioc_target") == null ? "" : sourceAsMap.get("ioc_target").toString())
                .setIoc_link(sourceAsMap.get("ioc_link") == null ? "" : sourceAsMap.get("ioc_link").toString())
                .setApt_org_final(apt_org_agg)
                .setIoc_type(sourceAsMap.get("ioc_type_agg") == null ? "" : sourceAsMap.get("ioc_type_agg").toString())
                .setPort(sourceAsMap.get("port_agg") == null ? "" : sourceAsMap.get("port_agg").toString())
                .setIoc_org_id(sourceAsMap.get("ioc_org_id") == null ? "" : sourceAsMap.get("ioc_org_id").toString())
                .setCategory_final(sourceAsMap.get("malicious_category") == null ? "" : sourceAsMap.get("malicious_category").toString())
                .setType(sourceAsMap.get("malicious_typename") == null ? "" : sourceAsMap.get("malicious_typename").toString())
        ;
        return intelligenceFinalRaw;
    }

    /**
     * true:
     * iocType:ip , port:0
     * iocType:ip , port:""
     * iocType:ip , port匹配excel中的目的端口destPort
     * <p>
     * iocType:domain , port:0
     * iocType:domain , port:""
     * iocType:domain , port:port匹配excel中的目的端口destPort,或者53
     * <p>
     * iocType:url
     *
     * @param intelligenceFinalRaw
     * @param outLog
     * @return
     */
    public Boolean checkPort(IntelligenceFinalRaw intelligenceFinalRaw, OutLog outLog) {
        if ("ip".equals(intelligenceFinalRaw.getIoc_type())) {
            if (Objects.equals(intelligenceFinalRaw.getPort(), "0")) {
                return true;
            } else if (Objects.equals(intelligenceFinalRaw.getPort(), "")) {
                return true;
            } else {
                String[] ports = intelligenceFinalRaw.getPort().split(",");
                for (String port : ports) {
                    String[] destPorts = outLog.getDestPort().split("\\|");
                    for (String destPort : destPorts) {
                        if (destPort.equals(port)) {
                            return true;
                        }
                    }

                }
            }
        } else if ("domain".equals(intelligenceFinalRaw.getIoc_type())) {
            if (Objects.equals(intelligenceFinalRaw.getPort(), "0")) {
                return true;
            } else if (Objects.equals(intelligenceFinalRaw.getPort(), "")) {
                return true;
            } else {
                String[] ports = intelligenceFinalRaw.getPort().split(",");
                for (String port : ports) {
                    if ("53".equals(outLog.getDestPort())) {
                        return true;
                    }
                    String[] destPorts = outLog.getDestPort().split("\\|");
                    for (String destPort : destPorts) {
                        if (destPort.equals(port)) {
                            return true;
                        }
                    }
                }
            }
        } else if ("url".equals(intelligenceFinalRaw.getIoc_type())) {
            return true;
        }
        return false;
    }

    public CluesAptIssued getAptObject(IntelligenceFinalRaw intelligenceFinalRaw,
                                       List<CluesAptIssued> aptList,
                                       Information information,
                                       OutLog outLog
    ) {
        return new CluesAptIssued()
                .setSn(aptList.size() + 1)
                .setProvince(information.getProvince() == null ? "" : information.getProvince())
                .setIndustry(information.getIndustry() == null ? "" : information.getIndustry())
                .setDeploymentLocation(information.getDeploymentLocation() == null ? "" : information.getDeploymentLocation())
                .setCompany("company.getCompany()")
                .setAptOrgName(intelligenceFinalRaw.getApt_org_final())
                .setAptOrgLocation(intelligenceFinalRaw.getApt_country())
                .setSrcHost(outLog.getSrcIp())
                .setSrcPort(outLog.getSrcPort())
                .setIoc(intelligenceFinalRaw.getIoc())
                .setDestPort(outLog.getDestPort())
                .setCountry(intelligenceFinalRaw.getIp_country())
                .setRegion(intelligenceFinalRaw.getIp_city())
                .setPlatform(intelligenceFinalRaw.getPlatform())
                .setDescription(intelligenceFinalRaw.getDescription())
                .setRiskLevel(intelligenceFinalRaw.getRisk_level())
                .setConfidence(intelligenceFinalRaw.getConfidence())
                .setStatusSurvival(intelligenceFinalRaw.getStatus())
                .setCreateTime(intelligenceFinalRaw.getCtime())
                .setUpdateTime(intelligenceFinalRaw.getUtime())
                .setExpireTime(intelligenceFinalRaw.getEtime())
                .setIocOrig(intelligenceFinalRaw.getIoc_org())
                .setSamples(intelligenceFinalRaw.getIoc_fileHash())
                .setAttackIntention(intelligenceFinalRaw.getIoc_target())
                .setIocLink(intelligenceFinalRaw.getIoc_link())
                .setFamily(intelligenceFinalRaw.getFamily())
                .setExternalCount(outLog.getExternalCount())
                .setFirstExternalTime(DateUtil.dateToStr(outLog.getFirstExternalTime(), "yyyy-MM-dd HH:mm:ss"))
                .setEndExternalTime(DateUtil.dateToStr(outLog.getEndExternalTime(), "yyyy-MM-dd HH:mm:ss"))
                .setVendor(intelligenceFinalRaw.getIoc_orig())
                .setIoc_type(intelligenceFinalRaw.getIoc_type());
    }
}

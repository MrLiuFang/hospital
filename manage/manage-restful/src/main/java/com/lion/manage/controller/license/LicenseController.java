package com.lion.manage.controller.license;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.annotation.AuthorizationIgnore;
import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.device.entity.device.Device;
import com.lion.device.entity.enums.DeviceClassify;
import com.lion.device.entity.enums.DeviceType;
import com.lion.device.entity.enums.State;
import com.lion.device.entity.enums.TagType;
import com.lion.device.entity.tag.Tag;
import com.lion.device.expose.cctv.CctvExposeService;
import com.lion.device.expose.device.DeviceExposeService;
import com.lion.device.expose.tag.TagExposeService;
import com.lion.manage.entity.license.AboutVo;
import com.lion.manage.entity.license.License;
import com.lion.manage.service.license.LicenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;
import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/license")
@Validated
@Api(tags = {"license管理"})
public class LicenseController extends BaseControllerImpl implements BaseController {

    private  String  public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbPckqYVqA0RjTH+Qid69aB/M3Sex4gCUGTxyVlwmL02Q16Ws7KvvwYZnaL5d8sfH5yztkvQOArEJdOblnT0/kouP/DakscD88ZEtmsPRzwLE6UsnzJ9vTysAA4wA21AWLA3+x67jJfxsC2dcu9aK5axpCW5NOWMybes3DckS82wIDAQAB";
    private  final String KEY_ALGORITHM = "RSA";
    java.util.Base64.Decoder base64Decoder= java.util.Base64.getMimeDecoder();
    private static final int MAX_DECRYPT_BLOCK = 128;

    @Autowired
    private LicenseService licenseService;
    @DubboReference
    private DeviceExposeService deviceExposeService;

    @DubboReference
    private CctvExposeService cctvExposeService;

    @DubboReference
    private TagExposeService tagExposeService;

    private String licensePath = "D:\\license\\";
//    private String licensePath = "/workspace/澳门医院/license/";
    private String fileName ="";

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/upload")
    @AuthorizationIgnore
    @ApiOperation(value = "上传license",notes = "上传license")
    public IResultData upload(@ApiIgnore StandardMultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
        String osName = System.getProperties().getProperty("os.name");
        if (osName.toLowerCase().indexOf("linux")>-1) {
            licensePath = "/workspace/澳门医院/license/";
        } else {
            licensePath = "D:\\license\\";
        }
        AtomicInteger tagCount = new AtomicInteger(0);
        AtomicInteger deviceCount = new AtomicInteger(0);
        int userCount = 0;
        fileName = UUID.randomUUID().toString();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, MultipartFile> files = multipartHttpServletRequest.getFileMap();
        for(String originalFileName : files.keySet()) {
            MultipartFile file = files.get(originalFileName);
            if (file.getSize() <= 0) {
                continue;
            }
            if (!new File(licensePath+fileName).exists()){
                new File(licensePath+fileName).mkdirs();
            }
            java.io.File file1 = new java.io.File(licensePath+fileName+".zip");
            file.transferTo(file1);
            unPacket(file1, Paths.get(licensePath+fileName));

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(licensePath+fileName+"/EquipmentOrder_EN1.lic")), "UTF-8"));
                String lineTxt = null;
                String str = "";
                // 逐行读取
                while ((lineTxt = br.readLine()) != null) {
                    if (Objects.nonNull(lineTxt)) {
                        str += lineTxt;
                    }
                }
                br.close();
                String equipmentOrder = decryptByPublicKeyStr(str,public_key);

                JsonNode jsonNode = objectMapper.readTree(equipmentOrder);
                JsonNode interfaceEquipmentOrderList = jsonNode.get("interfaceEquipmentOrderList");
                if (interfaceEquipmentOrderList.isArray()){
                    interfaceEquipmentOrderList.forEach(jsonNode1 -> {
                        String equipmentNo = jsonNode1.get("equipmentNo").asText();
                        Boolean b = false;
                        Device device = deviceExposeService.find(equipmentNo);
                        if (Objects.nonNull(device)) {
                            if (Objects.equals(State.NOT_ACTIVE,device.getDeviceState())) {
                                deviceExposeService.updateState(equipmentNo, State.ACTIVE);
                            }
                            b = true;
                        }
                        Tag tag = tagExposeService.find(equipmentNo);
                        if (Objects.nonNull(tag)) {
                            if (Objects.equals(State.NOT_ACTIVE,tag.getDeviceState())) {
                                tagExposeService.updateDeviceState(equipmentNo, State.ACTIVE);
                            }
                            b = true;
                        }
                        if (!b) {
//                            {"id":1,"name":"Time Star"},{"id":2,"name":"Standard Star"},{"id":3,"name":"Monitor"},{"id":4,"name":"Virtual Wall"},{"id":5,"name":"Hand Washing"},{"id":6,"name":"LF Exciter"},{"id":7,"name":"回收箱"},{"id":8,"name":"新生兒標籤"},{"id":9,"name":"普通標籤"},{"id":10,"name":"職員標籤"},{"id":11,"name":"溫濕標籤"},{"id":12,"name":"一次性標籤"},{"id":13,"name":"帶按鈕標籤"}
                            Integer equipmentId = jsonNode1.get("equipmentId").asInt();
                            List<Integer> deviceList = Arrays.asList(new Integer[]{1,2,3,4,5,6,7});
                            List<Integer> tagList = Arrays.asList(new Integer[]{8,9,10,11,12,13,14});
                            if (deviceList.contains(equipmentId)) {
                                Device entity = new Device();
                                entity.setDeviceState(State.ACTIVE);
                                entity.setCode(equipmentNo);
                                if (Objects.equals(equipmentId,1)){
                                    entity.setDeviceClassify(DeviceClassify.STAR_AP);
                                    entity.setDeviceType(DeviceType.TIME_STAR);
                                }else if (Objects.equals(equipmentId,2)){
                                    entity.setDeviceClassify(DeviceClassify.STAR_AP);
                                    entity.setDeviceType(DeviceType.STANDARD_STAR);
                                }else if (Objects.equals(equipmentId,3)){
                                    entity.setDeviceClassify(DeviceClassify.MONITOR);
                                }else if (Objects.equals(equipmentId,4)){
                                    entity.setDeviceClassify(DeviceClassify.VIRTUAL_WALL);
                                }else if (Objects.equals(equipmentId,5)){
                                    entity.setDeviceClassify(DeviceClassify.HAND_WASHING);
                                }else if (Objects.equals(equipmentId,6)){
                                    entity.setDeviceClassify(DeviceClassify.LF_EXCITER);
                                }else if (Objects.equals(equipmentId,7)){
                                    entity.setDeviceClassify(DeviceClassify.RECYCLING_BOX);
                                }
                                deviceCount.incrementAndGet();
                                deviceExposeService.save(entity);
                            }else if (tagList.contains(equipmentId)) {
//                                {"id":8,"name":"新生兒標籤"},{"id":9,"name":"普通標籤"},{"id":10,"name":"職員標籤"},{"id":11,"name":"溫濕標籤"},{"id":12,"name":"一次性標籤"},{"id":13,"name":"帶按鈕標籤"}
                                Tag entity1 = new Tag();
                                entity1.setTagCode(equipmentNo);
                                entity1.setDeviceState(State.ACTIVE);
                                if (Objects.equals(equipmentId,8)){
                                    entity1.setType(TagType.BABY);
                                }else if (Objects.equals(equipmentId,9)){
                                    entity1.setType(TagType.ORDINARY);
                                }else if (Objects.equals(equipmentId,10)){
                                    entity1.setType(TagType.STAFF);
                                }else if (Objects.equals(equipmentId,11)){
                                    entity1.setType(TagType.TEMPERATURE_HUMIDITY);
                                }else if (Objects.equals(equipmentId,12)){
                                    entity1.setType(TagType.DISPOSABLE);
                                }else if (Objects.equals(equipmentId,13)){
                                    entity1.setType(TagType.BUTTON);
                                }else if (Objects.equals(equipmentId,14)){
                                    entity1.setType(TagType.PREVENT_FALLING);
                                }
                                tagCount.incrementAndGet();
                                tagExposeService.save(entity1);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            interfaceEquipmentOrderList
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(licensePath+fileName+"/Order_EN1.lic")), "UTF-8"));
                String lineTxt1 = null;
                String str1 = "";
                while ((lineTxt1 = br1.readLine()) != null) {
                    if (Objects.nonNull(lineTxt1)) {
                        str1 += lineTxt1;
                    }
                }
                br1.close();
                String order = decryptByPublicKeyStr(str1,public_key);
                licenseService.deleteAll();
                JsonNode jsonNode1 = objectMapper.readTree(order);
                License license = new License();
                license.setEffectivTime(LocalDate.parse(jsonNode1.get("effectivTime").asText()));
                license.setStartDate(LocalDate.parse(jsonNode1.get("startDate").asText()));
                license.setEndDate(LocalDate.parse(jsonNode1.get("endDate").asText()));
                license.setMenuList(jsonNode1.get("interfacePermissionMenuList").toString());
                license.setUserNum(jsonNode1.get("userNum").asLong());
                userCount=license.getUserNum().intValue();
                license.setPersonInCharge(jsonNode1.get("personInCharge").asText());
                license.setWorkstationOrderList(jsonNode1.get("interfaceWorkstationOrderLsit").toString());
                license = licenseService.save(license);
                redisTemplate.opsForValue().set("license",license);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ResultData resultData = ResultData.instance();
        Map<String,Integer> map = new HashMap<>();
        map.put("tagCount",tagCount.get());
        map.put("deviceCount",deviceCount.get());
        map.put("userCount",userCount);
        resultData.setData(map);
        return resultData;
    }

    @GetMapping("/menu")
    @AuthorizationIgnore
    @ApiOperation(value = "获取license中的授权菜单",notes = "获取license中的授权菜单")
    public IResultData<String> getLicenseMenuList(){
        List<License> list = licenseService.findAll();
        if (list.size()>0) {
            License license = list.get(0);
            license.getMenuList();
            return ResultData.instance().setData(license.getMenuList());
        }
        return ResultData.instance();
    }

    @GetMapping("/about")
    @AuthorizationIgnore
    @ApiOperation(value = "关于页面",notes = "关于页面")
    public IResultData<AboutVo> about() throws JsonProcessingException {
        List<License> list = licenseService.findAll();
        if (list.size()>0) {
            License license = list.get(0);
            AboutVo aboutVo = new AboutVo();
            aboutVo.setActiveTime(license.getCreateDateTime().toLocalDate());
            aboutVo.setStartDate(license.getStartDate());
            aboutVo.setEndDate(license.getEndDate());
            aboutVo.setUserNum(license.getUserNum().intValue());
            aboutVo.setPersonInCharge(license.getPersonInCharge());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(license.getWorkstationOrderList());
            aboutVo.setWorkstations(license.getWorkstationOrderList());
            aboutVo.setWorkstation(jsonNode.size());
            aboutVo.setMenuList(license.getMenuList());
            aboutVo.setMonitorNum(deviceExposeService.count());
            aboutVo.setTagNum(tagExposeService.count());
            return ResultData.instance().setData(aboutVo);

        }
        return ResultData.instance();
    }



    private void unPacket(java.io.File file, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        // 创建zip对象
        ZipFile zipFile = new ZipFile(file);
        try {
            // 读取zip流
            try(ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file) )){
                ZipEntry zipEntry = null;
                // 遍历每一个zip项
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    // 获取zip项目名称
                    String entryName = zipEntry.getName();
                    // 构建绝对路径
                    Path entryFile = targetDir.resolve(entryName);
                    if(zipEntry.isDirectory()) {	// 文件夹
                        if (!Files.isDirectory(entryFile)) {
                            Files.createDirectories(entryFile);
                        }
                    } else {							// 文件
                        // 读取zip项数据流
                        try(InputStream zipEntryInputStream = zipFile.getInputStream(zipEntry)){
                            try(OutputStream fileOutputStream = Files.newOutputStream(entryFile, StandardOpenOption.CREATE_NEW)){
                                byte[] buffer = new byte[4096];
                                int length = 0;
                                while ((length = zipEntryInputStream.read(buffer)) != -1) {
                                    fileOutputStream.write(buffer, 0, length);
                                }
                                fileOutputStream.flush();
                            }
                        }
                    }
                }
            }
        } finally {
            zipFile.close();
        }
    }

    private String decryptByPublicKeyStr(String content, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        byte[] encryptedData = base64Decoder.decode(content);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData,"UTF-8");
    }

    public static void main(String[] agrs) throws Exception {
        LicenseController licenseController = new LicenseController();

        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/home/coby/下载/t/EquipmentOrder_EN10001.lic")), "UTF-8"));
        String lineTxt1 = null;
        String str1 = "";
        // 逐行读取
        while ((lineTxt1 = br1.readLine()) != null) {
            if (Objects.nonNull(lineTxt1)) {
                str1 += lineTxt1;
            }
        }
        br1.close();
        String order = licenseController.decryptByPublicKeyStr(str1,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbPckqYVqA0RjTH+Qid69aB/M3Sex4gCUGTxyVlwmL02Q16Ws7KvvwYZnaL5d8sfH5yztkvQOArEJdOblnT0/kouP/DakscD88ZEtmsPRzwLE6UsnzJ9vTysAA4wA21AWLA3+x67jJfxsC2dcu9aK5axpCW5NOWMybes3DckS82wIDAQAB");
        System.out.println(order);
    }
}

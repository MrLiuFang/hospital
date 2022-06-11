package com.lion.manage.controller.license;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.annotation.AuthorizationIgnore;
import com.lion.core.IResultData;
import com.lion.core.ResultData;
import com.lion.core.controller.BaseController;
import com.lion.core.controller.impl.BaseControllerImpl;
import com.lion.device.entity.cctv.Cctv;
import com.lion.device.entity.enums.State;
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

//    private String licensePath = "D:\\license\\";
    private String licensePath = "/workspace/澳门医院/license/";
    private String fileName ="";

    @PostMapping("/upload")
    @AuthorizationIgnore
    @ApiOperation(value = "上传license",notes = "上传license")
    public IResultData upload(@ApiIgnore StandardMultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
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
                        deviceExposeService.updateState(equipmentNo, State.ACTIVE);
                        tagExposeService.updateDeviceState(equipmentNo,State.ACTIVE);
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
                license.setPersonInCharge(jsonNode1.get("personInCharge").asText());
                license.setWorkstationOrderList(jsonNode1.get("interfaceWorkstationOrderLsit").toString());
                licenseService.save(license);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResultData.instance();
    }

    @GetMapping("/menu")
    @AuthorizationIgnore
    @ApiOperation(value = "获取license中的授权菜单",notes = "获取license中的授权菜单")
    public IResultData<String> getLicenseMenuList(){
        List<License> list = licenseService.findAll();
        if (list.size()>0) {
            License license = list.get(0);
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
            aboutVo.setCctvNum(cctvExposeService.count());
            aboutVo.setTagNum(tagExposeService.countActive());
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

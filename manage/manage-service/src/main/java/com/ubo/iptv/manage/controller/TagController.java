package com.ubo.iptv.manage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubo.iptv.core.AbstractController;
import com.ubo.iptv.core.CommonResponse;
import com.ubo.iptv.core.enums.SysEnum;
import com.ubo.iptv.manage.api.TagApi;
import com.ubo.iptv.manage.requset.TagConditionDTO;
import com.ubo.iptv.manage.response.*;
import com.ubo.iptv.manage.service.EsReportService;
import com.ubo.iptv.manage.service.TagService;
import io.swagger.annotations.ApiParam;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xuning
 * @Date: 2020-09-30
 */
@RestController
public class TagController extends AbstractController implements TagApi {

    @Autowired
    TagService tagService;
    @Autowired
    EsReportService esReportService;
    @Autowired
    private HttpServletResponse response;

    @Override
    public CommonResponse<Map<String, String>> userTagDivideList(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify,
                                                                 @ApiParam(value = "标签区分", required = true) @RequestParam(value = "type") Integer type) {
        return tagService.userTagDivideList(classify, type);
    }

    @Override
    public CommonResponse<TagSummaryDetailVO> tagSummaryDetail(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "tagIds") String tagIds) {
        return tagService.tagSummaryDetail(tagIds);
    }

    @Override
    public  CommonResponse<IPage<TagUserSummaryVO>> tagUserList(@ApiParam(value = "标签", required = true) @RequestParam(value = "tagIds") String tagIds,
                                                                @ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                                @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){
        return tagService.tagUserList(tagIds,new Page(current,size));
    }

    @Override
    public CommonResponse<UserTagImageVO> drawUserImage(@ApiParam(value = "用户id", required = true) @RequestParam(value = "userId") Long userId) {
        return tagService.drawUserImage(userId);
    }

    @Override
    public CommonResponse<IPage<GroupInfoVO>> userGroups(@ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                        @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return tagService.userGroups(new Page(current,size));
    }

    @Override
    public CommonResponse<UserGroupDetailVO> userGroupDetail(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        return tagService.userGroupDetail(groupId);
    }

    @Override
    public CommonResponse<List<TagMediaSummaryVO>> mediaGroupDetail(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        return tagService.mediaGroupDetail(groupId);
    }


    @Override
    public CommonResponse<IPage<GroupInfoVO>> mediaGroups(@ApiParam(value = "current") @RequestParam(value = "current", required = false, defaultValue = "1") Integer current,
                                                         @ApiParam(value = "size") @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return tagService.mediaGroups(new Page(current,size));
    }

    @Override
    public CommonResponse groupAdd(@ApiParam(value = "参数", required = true) @RequestBody @Valid TagConditionDTO dto) {
        return tagService.groupAdd(super.getUserId(), dto);
    }

    @Override
    public CommonResponse groupUpdate(@ApiParam(value = "参数", required = true) @RequestBody @Valid TagConditionDTO dto) {
        return tagService.groupUpdate(super.getUserId(), dto);
    }

    @Override
    public CommonResponse groupDelete(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        return tagService.groupDelete(groupId);
    }

    @Override
    public CommonResponse<TagConditionVO> groupSelect(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        return tagService.groupSelect(groupId);
    }


    @Override
    public CommonResponse<Set<String>> selectConditionDivide(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "classify") Integer classify) {
        return tagService.selectConditionDivide(classify);
    }

    @Override

    public CommonResponse<List<TagVO>> selectConditionDivideResult(@ApiParam(value = "标签划分", required = true) @RequestParam(value = "divide") String divide) {
        return tagService.selectConditionDivideResult(divide);
    }


    @Override
    public CommonResponse<Map<String, TagSummary>> tagSummarySort(@ApiParam(value = "标签分类", required = true) @RequestParam(value = "tagIds") String tagIds,
                                                                  @ApiParam(value = "排序日期") @RequestParam(value = "sortDate", required = false) String sortDate,
                                                                  @ApiParam(value = "是否正序") @RequestParam(value = "sortAsc", required = false) Boolean sortAsc) {
        return tagService.tagSummarySort(tagIds, sortDate, sortAsc);
    }


    @Override
    public CommonResponse saveDailyGroupStatus(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        return tagService.saveDailyGroupStatus(groupId);
    }


    @Override
    public void exportUserGroup(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");
        // 表头
        int rowNum = 0;
        XSSFRow headRow = sheet.createRow(rowNum++);
        int column = 0;
        XSSFCell headCell2 = headRow.createCell(column++);
        headCell2.setCellValue("用户id");
        XSSFCell headCell3 = headRow.createCell(column++);
        headCell3.setCellValue("地址");
        XSSFCell headCell4 = headRow.createCell(column++);
        headCell4.setCellValue("激活时间");
        XSSFCell headCell5 = headRow.createCell(column++);
        headCell5.setCellValue("运营商");

        // 内容
        SearchResponse searchResponse = esReportService.downLoadGroupInfo(groupId);
        while (true) {
            if (searchResponse == null || searchResponse.getHits().getHits().length == 0) {
                break;
            }
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                JSONObject jsonObject = JSON.parseObject(json);
                XSSFRow row = sheet.createRow(rowNum++);
                column = 0;
                //用户id
                XSSFCell cell2 = row.createCell(column++);
                cell2.setCellValue(StringUtils.isEmpty(jsonObject.getString("userId")) ? "" : jsonObject.getString("userId"));
                //地址
                XSSFCell cell3 = row.createCell(column++);
                cell3.setCellValue(StringUtils.isEmpty(jsonObject.getString("cityName")) ? "" : jsonObject.getString("cityName"));
                //激活时间
                XSSFCell cell4 = row.createCell(column++);
                cell4.setCellValue(StringUtils.isEmpty(jsonObject.getString("activeTime")) ? "" : jsonObject.getString("activeTime"));
                //运营商
                XSSFCell cell5 = row.createCell(column++);
                cell5.setCellValue(StringUtils.isEmpty(jsonObject.getString("sysId")) ? "" : SysEnum.description(jsonObject.getString("sysId")));
            }
            String scrollId = searchResponse.getScrollId();
            searchResponse = esReportService.downLoadGroupInfo(scrollId);
        }
        // 输出excel
        try (OutputStream outputStream = response.getOutputStream()) {
            String filename = "UserGroup.xlsx";
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType("application/msexcel");
            wb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportMediaGroup(@ApiParam(value = "分组id", required = true) @RequestParam(value = "groupId") Long groupId) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Sheet1");
        // 表头
        int rowNum = 0;
        XSSFRow headRow = sheet.createRow(rowNum++);
        int column = 0;
        XSSFCell headCell2 = headRow.createCell(column++);
        headCell2.setCellValue("媒资id");
        XSSFCell headCell3 = headRow.createCell(column++);
        headCell3.setCellValue("媒资名称");
        XSSFCell headCell4 = headRow.createCell(column++);
        headCell4.setCellValue("媒资类型");
        XSSFCell headCell5 = headRow.createCell(column++);
        headCell5.setCellValue("媒资导演");
        XSSFCell headCell6 = headRow.createCell(column++);
        headCell6.setCellValue("媒资演员");
        XSSFCell headCell7 = headRow.createCell(column++);
        headCell7.setCellValue("媒资发行年代");
        XSSFCell headCell8 = headRow.createCell(column++);
        headCell8.setCellValue("是否收费");

        // 内容
        SearchResponse searchResponse = esReportService.downLoadGroupInfo(groupId);
        while (true) {
            if (searchResponse == null || searchResponse.getHits().getHits().length == 0) {
                break;
            }
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                JSONObject jsonObject = JSON.parseObject(json);
                XSSFRow row = sheet.createRow(rowNum++);
                column = 0;
                //媒资id
                XSSFCell cell2 = row.createCell(column++);
                cell2.setCellValue(StringUtils.isEmpty(jsonObject.getString("mediaId")) ? "" : jsonObject.getString("mediaId"));
                //媒资名称
                XSSFCell cell3 = row.createCell(column++);
                cell3.setCellValue(StringUtils.isEmpty(jsonObject.getString("name")) ? "" : jsonObject.getString("name"));
                //媒资类型
                XSSFCell cell4 = row.createCell(column++);
                cell4.setCellValue(StringUtils.isEmpty(jsonObject.getString("mediaTypeName")) ? "" : jsonObject.getString("mediaTypeName"));
                //媒资导演
                XSSFCell cell5 = row.createCell(column++);
                cell5.setCellValue(StringUtils.isEmpty(jsonObject.getString("director"))?"":jsonObject.getString("director").replace("\"", "").replace("[", "").replace("]", ""));
                //媒资演员
                XSSFCell cell6 = row.createCell(column++);
                cell6.setCellValue(StringUtils.isEmpty(jsonObject.getString("actor"))?"":jsonObject.getString("actor").replace("\"", "").replace("[", "").replace("]", ""));
                //媒资发行年代
                XSSFCell cell7 = row.createCell(column++);
                cell7.setCellValue(StringUtils.isEmpty(jsonObject.getString("releaseYear")) ? "" : jsonObject.getString("releaseYear"));
                //是否收费
                XSSFCell cell8 = row.createCell(column++);
                cell8.setCellValue(StringUtils.isEmpty(jsonObject.getString("free")) ? "" : jsonObject.getBoolean("free") ? "否" : "是");
            }
            String scrollId = searchResponse.getScrollId();
            searchResponse = esReportService.downLoadGroupInfo(scrollId);
        }

        // 输出excel
        try (OutputStream outputStream = response.getOutputStream()) {
            String filename = "MediaGroup.xlsx";
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType("application/msexcel");
            wb.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

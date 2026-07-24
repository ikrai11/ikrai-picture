package com.ikrai.ikraipicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikrai.ikraipicturebackend.model.dto.picture.PictureQueryRequest;
import com.ikrai.ikraipicturebackend.model.dto.picture.PictureReviewRequest;
import com.ikrai.ikraipicturebackend.model.dto.picture.PictureUploadByBatchRequest;
import com.ikrai.ikraipicturebackend.model.dto.picture.PictureUploadRequest;
import com.ikrai.ikraipicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ikrai.ikraipicturebackend.model.entity.User;
import com.ikrai.ikraipicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param inputSource 文件输入源，可以是文件路径或 URL
     * @param pictureUploadRequest 上传请求参数
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取单个图片脱敏
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片校验
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 将查询请求转化为Wrapper对象
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                 User loginUser);
}

package com.lion.event.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.pdf.*;

import java.io.IOException;

public class PdfPageEventHelper extends com.itextpdf.text.pdf.PdfPageEventHelper {
    // 模板
    public PdfTemplate tpl ;

    private String fontPath;

    private String username;

    public PdfPageEventHelper(String fontPath, String username) {
        this.fontPath = fontPath;
        this.username = username;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {

        try {
            tpl = writer.getDirectContent().createTemplate(100, 130);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        float center = document.getPageSize().getRight() / 2;//页面的水平中点
        float bottom = document.getPageSize().getBottom() + 20;
        //在每页结束的时候把“第x页”信息写道模版指定位置
        PdfContentByte cb = writer.getDirectContent();
        String text = writer.getPageNumber() + "/";
        cb.beginText();
        try {
            cb.setFontAndSize(BaseFont.createFont(fontPath+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 8);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cb.setTextMatrix(center, bottom);//定位“第x页,共” 在具体的页面调试时候需要更改这xy的坐标
        cb.showText(text);
        cb.endText();
        cb.addTemplate(tpl, center + 8, bottom);//定位“y页” 在具体的页面调试时候需要更改这xy的坐标
        cb.stroke();
        cb.saveState();
        cb.restoreState();
        cb.closePath();
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        //关闭document的时候获取总页数，并把总页数按模版写道之前预留的位置
        tpl.beginText();
        try {
            tpl.setFontAndSize(BaseFont.createFont(fontPath+",1",BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 8);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tpl.showText(Integer.toString(writer.getPageNumber())+"         工作人员:"+username);
        tpl.endText();
        tpl.closePath();//sanityCheck();
    }
}

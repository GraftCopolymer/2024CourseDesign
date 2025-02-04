package com.teach.javafx.customWidget;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class CourseTable extends GridPane {
    List<List<CourseCell>> courseCell = new ArrayList<>();
    private StackPane tableHead;


    public CourseTable(){
        super();
        for (int i = 0; i < 6; i++){
            List<CourseCell> innerList = new ArrayList<>();
            for(int j = 0 ; j < 8; j++){
                innerList.add(new CourseCell());
            }
            courseCell.add(innerList);
        }
        //初始化课程表骨架：节次列和星期行
        for(int i = 1; i < courseCell.get(0).size(); i++){
            add(new CourseCell(new Label(dayTrans(i))), i, 0);
        }
        for(int i = 1; i < courseCell.size(); i++){
            add(new CourseCell(new Label(String.valueOf(i))), 0, i);
        }
    }

    //课程需要有上课星期，节次，以此来定位
    public CourseTable(List<Course> course){
        super();
        //初始化一个6 x 8的二维数组
        for (int i = 0; i < 6; i++){
            List<CourseCell> innerList = new ArrayList<>();
            for(int j = 0 ; j < 8; j++){
                innerList.add(new CourseCell());
            }
            courseCell.add(innerList);
        }
        for(Course c : course){
            if(c.getCourseTimes().isEmpty()){
                continue;
            }
            Color color = genRandomShallowColor();
            for(CourseTime ct : c.getCourseTimes()){
                int day = ct.getDay(); //列
                int section = ct.getSection(); //行
                List<CourseCell> row = courseCell.get(section);
                row.set(day, new CourseCell(c, color));
            }
        }
        for(int i = 1; i < courseCell.size(); i++){
            for(int j = 1 ; j < courseCell.get(0).size(); j++){
                add(courseCell.get(i).get(j), j, i);
            }
        }
        for (int i = 0; i < courseCell.get(0).size(); i++){
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / 8.0);
            getColumnConstraints().add(columnConstraints);
        }
        for (int i = 0; i < courseCell.size(); i++){
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / 6.0);
            getRowConstraints().add(rowConstraints);
        }
    }

    public static Color genRandomShallowColor(){
        Random random = new Random();
        double red = 1.0 - random.nextDouble(0.4);
        double green = 1.0 - random.nextDouble(0.4);
        double blue = 1.0 - random.nextDouble(0.4);
        return new Color(red, green, blue, 1);
    }

    //输入数字，根据数字返回星期的名称
    public static String dayTrans(int day){
        switch (day){
            case 1:return "星期一";
            case 2:return "星期二";
            case 3:return "星期三";
            case 4:return "星期四";
            case 5:return "星期五";
            case 6:return "星期六";
            case 7:return "星期天";
            default:return "星期零";
        }
    }

    //向课程表中添加课程，其中背景颜色若传入null则在内部随机一个背景颜色
    //注意，若该课程无上课时间，则不会添加到课程表中
    public void addAllCourse(List<Course> cl, Color colorOut){
        //清空现有课程单元
        courseCell.clear();
        //生成新课程单元
        for (int i = 0; i < 6; i++){
            List<CourseCell> innerList = new ArrayList<>();
            for(int j = 0 ; j < 8; j++){
                innerList.add(new CourseCell());
            }
            courseCell.add(innerList);
        }
        //初始化课程表骨架：节次列和星期行
        for(int i = 1; i < courseCell.get(0).size(); i++){
            add(new CourseCell(new Label(dayTrans(i))), i, 0);
        }
        for(int i = 1; i < courseCell.size(); i++){
            add(new CourseCell(new Label(String.valueOf(i))), 0, i);
        }
        add(new CourseCell(),0,0);

        CourseCell cell = courseCell.get(0).get(0);
        add(getTableHead(cell.getPrefWidth(), cell.getPrefHeight()),0,0);


        for(Course c : cl){
            if(c.getCourseTimes().isEmpty()){
                continue;
            }
            Color color;
            if(colorOut == null){
                 color = genRandomShallowColor();
            }
            else {
                color = colorOut;
            }
            for(CourseTime ct : c.getCourseTimes()){
                int day = ct.getDay(); //列
                int section = ct.getSection(); //行
                List<CourseCell> row = courseCell.get(section);
                row.set(day, new CourseCell(c, color));
            }
        }
        for(int i = 1; i < courseCell.size(); i++){
            for(int j = 1 ; j < courseCell.get(0).size(); j++){
                add(courseCell.get(i).get(j), j, i);
            }
        }
    }

    private AnchorPane getTableHead(double width, double height){
        AnchorPane res = new AnchorPane();
        BackgroundFill bgf = new BackgroundFill(Color.WHITE,CornerRadii.EMPTY, Insets.EMPTY);
        Background bg = new Background(bgf);

        StackPane background = new StackPane();
        //设置首选宽度
        background.setPrefWidth(width);
        background.setPrefHeight(height);
        //背景颜色
        background.setBackground(bg);
        //边框
        BorderStroke bs = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT);
        Border border = new Border(bs);
        background.setBorder(border);
        //绝对定位
        background.setLayoutX(0);
        background.setLayoutY(0);
        res.getChildren().add(background);

        Line line = new Line(0,0,width,height);
        //设置实现宽度
        line.setStrokeWidth(2);
        background.getChildren().add(line);

        Label day = new Label("星期");
        day.setLayoutX(1.1*width/2.0);
        day.setLayoutY(height/3.0);
        Label section = new Label("节次");
        section.setLayoutX(width/4.3);
        section.setLayoutY(2.0*height/3.0);
        res.getChildren().add(day);
        res.getChildren().add(section);
        return res;
    }

    public void addCourse(Course c, Color outColor){
        if(c.getCourseTimes() == null || c.getCourseTimes().isEmpty()){
            return;
        }
        Color color;
        if(outColor == null){
            color = genRandomShallowColor();
        }
        else {
            color = outColor;
        }
        for(CourseTime ct : c.getCourseTimes()){
            CourseCell cc = new CourseCell(c, color);
            List<CourseCell> ccl = courseCell.get(ct.getSection());
            ccl.add(ct.getDay(), cc);
            add(cc, ct.getDay(), ct.getSection());
        }
    }

    public void removeCourse(Course c){
        if(c.getCourseTimes() == null || c.getCourseTimes().isEmpty()){
            return;
        }
        for(CourseTime ct: c.getCourseTimes()){
            int row = ct.getSection();
            int column = ct.getDay();
            add(new CourseCell(), column, row);
        }
    }

    //抛弃之前的课程，用新的课程更新课程表
    public void updateTable(List<Course> cs){

    }

    public void printCourseInfos(){
        for(int i = 0 ; i < 6; i++){
            for(int j = 0; j < 8 ; j++){
                System.out.println(courseCell.get(i).get(j).getCourseName());
            }
        }
    }

//    public void setGrid(List<>){
//
//    }
}

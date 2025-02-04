package org.fatmansoft.teach.models;

import java.util.Map;
import java.util.Objects;

public class Teacher {
    private Integer teacherId;
    private Person person;
    private String personName;
    private String title;
    private String degree;

    public Teacher(){

    }

    public Teacher(Map m){
        if(m.get("teacherId") != null){
            this.teacherId = Integer.parseInt((String) m.get("teacherId"));
        }
        else{
            this.teacherId = null;
        }
        Map personMap = (Map) m.get("person");
        if(personMap != null){
            this.personName = (String) personMap.get("name");
            this.title = (String) m.get("title");
            this.degree = (String) m.get("degree");
        }
        else{
            this.personName = null;
            this.title = null;
            this.degree = null;
        }
    }

    //判断当前对象是否为空老师
    //teacherId小于等于0或null时则为空老师
    public boolean isEmptyTeacher(){
        return teacherId == null || teacherId <= 0;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Override
    public String toString(){
        if(teacherId == null || teacherId <= 0){
            return "无";
        }
        return teacherId + " - " + personName;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Teacher)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(Objects.equals(((Teacher) o).getTeacherId(), this.teacherId)){
            return true;
        }
        return false;
    }
}

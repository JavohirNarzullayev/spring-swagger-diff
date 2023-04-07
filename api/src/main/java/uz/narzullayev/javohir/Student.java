package uz.narzullayev.javohir;


import io.swagger.v3.oas.annotations.media.Schema;

public class Student implements Employer{
    @Schema(description = "Id of student")
    private Integer id;


    private String fio;

    private Car car;
    public static class Car{
        private String fio;

    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}

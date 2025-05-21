package com.example.controle_de_cadastro;

public class Evento {
    private String id;
    private String nome;
    private String dataInicio;   // formato "dd/MM/yyyy"
    private String dataTermino;  // formato "dd/MM/yyyy"
    private String horaInicio;   // formato "HH:mm"
    private String horaTermino;  // formato "HH:mm"

    public Evento() {
    }

    public Evento(String id, String nome, String dataInicio, String dataTermino, String horaInicio, String horaTermino) {
        this.id = id;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }
}

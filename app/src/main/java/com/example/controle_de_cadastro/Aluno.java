package com.example.controle_de_cadastro;

public class Aluno {
    public String nome, email, cpf, password, tipoUsuario;

    public Aluno() {
    }

    public Aluno(String nome, String email, String cpf, String password, String tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.password = password;
        this.tipoUsuario = tipoUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}

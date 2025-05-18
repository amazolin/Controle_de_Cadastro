package com.example.controle_de_cadastro;


    public interface AlunoCallback {
        void onAlunoEncontrado(Aluno aluno);
        void onAlunoNaoEncontrado();
        void onErro(Exception e);
    }



import java.time.LocalDate;
import java.time.LocalTime;

public class Evento {
    private String nome;
    private LocalDate data;
    private LocalTime horaInicio;
    private LocalTime horaTermino;

    public Evento() {
    }

    public Evento(String nome, LocalDate data, LocalTime horaInicio, LocalTime horaTermino) {
        this.nome = nome;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaTermino = horaTermino;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(LocalTime horaTermino) {
        this.horaTermino = horaTermino;
    }
}

// Updated ReporteDTO.java

public class ReporteDTO {
    private String fecha; // Changed from LocalDateTime to String
    private Boolean estado; // Changed from String to Boolean
    
    // Constructor, getters and setters
    public ReporteDTO(String fecha, Boolean estado) {
        this.fecha = fecha;
        this.estado = estado;
    }
    
    public String getFecha() {
        return fecha;
    }
    
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
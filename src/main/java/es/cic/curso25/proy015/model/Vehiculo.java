package es.cic.curso25.proy015.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tipo;
    private String marca;
    private String modelo;
    private String color;

    // Plaza que tiene asignada el vehículo
    @ManyToOne()
    @JoinColumn(name = "plaza_id")
    private Plaza plazaAsignada;

    // Plaza donde está realmente aparcado
    @OneToOne(mappedBy = "vehiculoOcupado")
    private Plaza plazaOcupada;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Multa> multas = new ArrayList<>();

    public void addMulta(Multa multa) {
        if (multa != null) {
            multas.add(multa);
            multa.setVehiculo(this);
        }
    }

    public void removeMulta(Multa multa) {
        if (multa != null && multas.remove(multa)) {
            multa.setVehiculo(null);
        }
    }

    public void setPlazaAsignada(Plaza nuevaPlaza) {
        if (this.plazaAsignada != null) {
            this.plazaAsignada.quitarVehiculo(this); // quitar de la lista anterior
        }
        this.plazaAsignada = nuevaPlaza;
        if (nuevaPlaza != null && !nuevaPlaza.getVehiculosAsignados().contains(this)) {
            nuevaPlaza.asignarVehiculo(this); // añadir a la nueva lista
        }
    }

    public void setPlazaOcupada(Plaza nuevaPlaza) {
        if (this.plazaOcupada != null) {
            this.plazaOcupada.ocuparConVehiculo(null);
        }
        this.plazaOcupada = nuevaPlaza;
        if (nuevaPlaza != null && nuevaPlaza.getVehiculoOcupante() != this) {
            nuevaPlaza.ocuparConVehiculo(this);
        }
    }

    public Vehiculo() {
    }

    public Vehiculo(String tipo, String marca, String modelo, String color, boolean aparcado) {
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Plaza getPlazaAsignada() {
        return plazaAsignada;
    }

    public Plaza getPlazaOcupada() {
        return plazaOcupada;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Multa> getMultas() {
        return multas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vehiculo other = (Vehiculo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vehiculo [id=" + id + ", tipo=" + tipo + ", marca=" + marca + ", modelo=" + modelo + ", color=" + color
                + "]";
    }

}

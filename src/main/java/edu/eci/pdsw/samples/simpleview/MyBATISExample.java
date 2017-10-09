/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.pdsw.samples.simpleview;

import edu.eci.pdsw.persistence.impl.mappers.EpsMapper;
import edu.eci.pdsw.persistence.impl.mappers.PacienteMapper;
import edu.eci.pdsw.samples.entities.Consulta;
import edu.eci.pdsw.samples.entities.Eps;
import edu.eci.pdsw.samples.entities.Paciente;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 *
 * @author hcadavid
 */
public class MyBATISExample {

/**
     * Método que construye una fábrica de sesiones de MyBatis a partir del
     * archivo de configuración ubicado en src/main/resources
     *
     * @return instancia de SQLSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        SqlSessionFactory sqlSessionFactory = null;
        if (sqlSessionFactory == null) {
            InputStream inputStream;
            try {
                inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(),e);
            }
        }
        return sqlSessionFactory;
    }

    /**
     * Programa principal de ejempo de uso de MyBATIS
     * @param args
     * @throws SQLException 
     */
    public static void main(String args[]) throws SQLException {
        SqlSessionFactory sessionfact = getSqlSessionFactory();
        SqlSession sqlss = sessionfact.openSession();
        PacienteMapper pmapper=sqlss.getMapper(PacienteMapper.class);
        EpsMapper emapper=sqlss.getMapper(EpsMapper.class);

        List<Paciente> pacientes=pmapper.loadPacientes();
        List<Eps> eps=emapper.loadAllEPS();

        //imprimir contenido de la lista
        for (Paciente p:pacientes){
            System.out.println(p.getId()+" "+p.getNombre());
        }
        
        //imprimir eps
        for (Eps e:eps){
            System.out.println(e.getNombre());
        }
        
        Paciente paciente=pmapper.loadPacienteById(1026585441, "CC");
        System.out.println("-------------------------");
        System.out.println(paciente.getId()+" "+paciente.getNombre()+"  "+paciente.getEps().getNombre()+"\nConsultas: ");
        for (Consulta c:paciente.getConsultas()){
            System.out.println(c.getId()+" "+c.getResumen());
        }
        
        //registrarNuevoPaciente(pmapper,new Paciente(225300,"CC","Pepito",java.sql.Date.valueOf("1956-05-01"),paciente.getEps()));
        
        Paciente pacienteNuevo=pmapper.loadPacienteById(225300,"CC");
        System.out.println(pacienteNuevo.getNombre());
        
        pmapper.insertConsulta(new Consulta(java.sql.Date.valueOf("1956-05-01"),"Dolor de cabeza",2555), 225300, "CC");
        System.out.println(pacienteNuevo.getConsultas());
        
        actualizarPaciente(pmapper,pmapper.loadPacienteById(12222, "CC"));
        
        sqlss.commit();
    }

    /**
     * Registra un nuevo paciente y sus respectivas consultas (si existiesen).
     * @param pmap mapper a traves del cual se hará la operacion
     * @param p paciente a ser registrado
     */
    public static void registrarNuevoPaciente(PacienteMapper pmap, Paciente p){
        pmap.insertarPaciente(p);
    }
    
    /**
    * @obj Actualizar los datos básicos del paciente, con sus * respectivas consultas.
     * @pre El paciente p ya existe
    * @param pmap mapper a traves del cual se hará la operacion
    * @param p paciente a ser registrado
    */
    public static void actualizarPaciente(PacienteMapper pmap, Paciente p){
        List<Consulta> consultas=(List)p.getConsultas();
        for (Consulta c: consultas){
            if (c.getId()==0){
                pmap.insertConsulta(c, p.getId(),p.getTipoId());
            }
        }
        pmap.actualizarPaciente(p.getId(), p.getTipoId(), "Kevin", p.getEps(),p);
    }
    
}

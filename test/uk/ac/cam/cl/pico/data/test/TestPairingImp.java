package uk.ac.cam.cl.pico.data.test;

import java.io.IOException;
import java.util.Date;

import uk.ac.cam.cl.pico.data.pairing.PairingImp;
import uk.ac.cam.cl.pico.data.service.Service;

public class TestPairingImp implements PairingImp {

    private static int nextId = 0;

    private boolean isSaved = false;
    private int id = nextId++;
    private String name;
    private Service service;
    private Date dateCreated = new Date();

    TestPairingImp(String name, Service service) {
        this.name = name;
        this.service = service;
    }

    @Override
    public void save() throws IOException {
        isSaved = true;
    }

    @Override
    public boolean isSaved() {
        return isSaved;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public void delete() throws IOException {
        // TODO Auto-generated method stub
        
    }
}
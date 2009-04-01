/**
 * Copyright (c) 2008, University of Bristol
 * Copyright (c) 2008, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package net.crew_vre.recorder.dao.impl;

import java.util.List;
import java.util.Vector;

import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import net.crew_vre.constants.CrewConstants;
import net.crew_vre.domain.JenaFiller;
import net.crew_vre.jena.vocabulary.Crew;
import net.crew_vre.recorder.dao.DeviceDao;
import net.crew_vre.recorder.domain.Device;

/**
 * A DAO for accessing devices
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class DeviceDaoImpl implements DeviceDao {

    private final String findDeviceSparql;

    private Database database = null;

    /**
     * Creates a new DeviceDaoImpl
     * @param database The database
     */
    public DeviceDaoImpl(Database database) {
        this.database = database;
        this.findDeviceSparql = Utils.loadSparql(
                "/sparql/findDevices.rq");
    }

    /**
     * @see net.crew_vre.recorder.dao.DeviceDao#addDevice(
     *     net.crew_vre.recorder.domain.Device)
     */
    public void addDevice(Device device) {
        Model model = database.getUpdateModel();
        String uri = device.getUri();
        if (uri == null) {
            uri = "http://localhost/devices/" + System.currentTimeMillis()
                 + (int) (Math.random() * CrewConstants.ID_NORMALIZATION);
        }
        Resource resource = model.createResource(uri);
        resource.addProperty(RDF.type, Crew.TYPE_DEVICE);
        resource.addProperty(DC.title, device.getName());
        resource.addProperty(DC.identifier, device.getId());

        database.addModel(null, model);
    }

    /**
     * @see net.crew_vre.recorder.dao.DeviceDao#findDevices()
     */
    public List<Device> findDevices() {
        Vector<Device> devices = new Vector<Device>();
        Results res = database.executeSelectQuery(findDeviceSparql, null);
        ResultSet results = res.getResults();
        while (results.hasNext()) {
            QuerySolution solution = results.nextSolution();
            Device device = new Device();
            JenaFiller.fillIn(device, solution, results.getResultVars());
            devices.add(device);
        }
        res.close();
        return devices;
    }
}

/*
 * Copyright 2020-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gnmitest.app;

import com.google.common.util.concurrent.Futures;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import gnmi.Gnmi;
import gnmi.Gnmi.GetRequest;
import gnmi.Gnmi.Path;
import org.onosproject.gnmi.api.GnmiClient;
import org.onosproject.gnmi.api.GnmiController;
import org.onosproject.gnmi.api.GnmiEvent;
import org.onosproject.net.DeviceId;
import org.onosproject.gnmi.api.GnmiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true, service = Testgnmi.class)
public final class Testgnmi {

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected GnmiController gnmiController;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private DeviceId deviceId = DeviceId.deviceId("device:s1");
    private GnmiClient gnmiClient;
    private GnmiEventListener deviceListener = new InnerDeviceListener();


    private static final GetRequest GET = GetRequest.newBuilder().addPath(
            Path.newBuilder().addElem(
                    Gnmi.PathElem.newBuilder().setName("/interfaces/interface[name=s1-eth1]/config/").build()
            ).build()).build();

    Path path = Path.newBuilder()
            .addElem(Gnmi.PathElem.newBuilder().setName("interfaces").build())
            .addElem(Gnmi.PathElem.newBuilder().setName("interface").putKey("name", "s1-eth1").build())
            .addElem(Gnmi.PathElem.newBuilder().setName("state").build())
            .build();


    @Activate
    public void activate() {
        log.info("############## ATTIVATO ###########" + gnmiController.toString());
        gnmiController.addListener(deviceListener);
        gnmiClient = gnmiController.get(deviceId);
        log.info("############## ATTIVATO ###########" + gnmiClient.toString());
        log.info("GET PATH" + path.toString());

        GetRequest request = GetRequest.newBuilder()
                .addPath(path)
                //.setType(GetRequest.DataType.ALL)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        log.info("NEW PATH" + Futures.getUnchecked(gnmiClient.get(request)).toString());


    }

    @Deactivate
    public void deactivate() {

    }

    private void checkSubscription(DeviceId deviceId) {
        if (gnmiController.get(deviceId) == null) {
            // Ignore devices for which a gNMI client does not exist.
            return;
        }

    }

    private class InnerDeviceListener implements GnmiEventListener {

        @Override
        public void event(GnmiEvent event) {

            log.info("################## GNMI EVENT ##################" + event.toString());
        }

    }
}

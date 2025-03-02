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
//import org.onosproject.net.Port;
//import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
//import gnmi.Gnmi.SubscriptionList;
//import gnmi.Gnmi.SubscriptionMode;
//import gnmi.Gnmi.Subscription;
import gnmi.Gnmi;
import gnmi.Gnmi.GetRequest;
import gnmi.Gnmi.SetRequest;
import gnmi.Gnmi.Path;
import gnmi.Gnmi.Update;
import gnmi.Gnmi.TypedValue;
//import gnmi.Gnmi.SubscribeRequest;
import org.onosproject.gnmi.api.GnmiClient;
import org.onosproject.gnmi.api.GnmiController;
import org.onosproject.gnmi.api.GnmiEvent;
import org.onosproject.net.DeviceId;
import org.onosproject.gnmi.api.GnmiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.util.Set;
//import java.util.stream.Collectors;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true, service = Testgnmi.class)
public final class Testgnmi {

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected GnmiController gnmiController;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private DeviceId deviceId = DeviceId.deviceId("device:s1");
    private GnmiClient gnmiClient;
    private GnmiEventListener deviceListener = new InnerDeviceListener();


    Path path = Path.newBuilder()
            .addElem(Gnmi.PathElem.newBuilder().setName("interfaces").build())
            .addElem(Gnmi.PathElem.newBuilder().setName("interface").putKey("name", "s1-eth1").build())
            .addElem(Gnmi.PathElem.newBuilder().setName("state").build())
            .addElem(Gnmi.PathElem.newBuilder().setName("oper-status").build())
            .build();

    Path qos = Path.newBuilder()
            .build();

    Path comp = Path.newBuilder()
            .addElem(Gnmi.PathElem.newBuilder().setName("components").build())
            .build();


    @Activate
    public void activate() {
        log.info("############## ATTIVATO ###########" + gnmiController.toString());
        gnmiController.addListener(deviceListener);
        gnmiClient = gnmiController.get(deviceId);
        log.info("############## ATTIVATO ###########" + gnmiClient.toString());
        //log.info("GET PATH" + path.toString());

        // indagare meglio
        GetRequest request = GetRequest.newBuilder()
                .addPath(path)
                //.setType(GetRequest.DataType.ALL)
                .setType(GetRequest.DataType.CONFIG)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        log.info("NEW PATH" + Futures.getUnchecked(gnmiClient.get(request)).toString());

        GetRequest requestplat = GetRequest.newBuilder()
                .addPath(comp)
                .setType(GetRequest.DataType.ALL)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        log.info("NEW PATH COMP" + Futures.getUnchecked(gnmiClient.get(requestplat)).toString());

        GetRequest requestqos = GetRequest.newBuilder()
                .addPath(qos)
                .setType(GetRequest.DataType.CONFIG)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        log.info("NEW PATH QOS" + Futures.getUnchecked(gnmiClient.get(requestqos)).toString());


        //log.info("CAPABILITIES" + Futures.getUnchecked(gnmiClient.capabilities()).toString());

        log.info("CAPABILITIES" + Futures.getUnchecked(gnmiClient.capabilities()).toString());
        log.info("CAPABILITIES default instance " + Futures.getUnchecked(gnmiClient.capabilities())
                .getDefaultInstanceForType().toString());
        log.info("CAPABILITIES model list" + Futures.getUnchecked(gnmiClient.capabilities())
                .getSupportedModelsList().toString());
        log.info("CAPABILITIES extentsion list" + Futures.getUnchecked(gnmiClient.capabilities())
                .getExtensionList().toString());
        log.info("CAPABILITIES gnmi version" + Futures.getUnchecked(gnmiClient.capabilities())
                .getGNMIVersion());
        /* ok ma campo selezionato non scrivibile
        SetRequest set = SetRequest.newBuilder()
                .addUpdate(Update.newBuilder().
                        setPath(path).setVal(TypedValue.newBuilder().setStringVal("3").build())).build();

        gnmiClient.set(set);
        */
        /**
         * Subscription code non serve perchè c'è già di partenza
         */

        /*Set<PortNumber> ports = deviceService.getPorts(deviceId).stream()
                .map(Port::number)
                .collect(Collectors.toSet());*/

       //log.info("porte" + ports.toString() + ports.iterator().next().name());
        /*
        gnmiController.get(deviceId).unsubscribe();

        SubscriptionList subscriptionList = SubscriptionList.newBuilder()
                .setMode(SubscriptionList.Mode.STREAM)
                .setUpdatesOnly(true)
               /* .addAllSubscription(ports.stream().map(
                        port -> Subscription.newBuilder()
                                .setPath(interfaceOperStatusPath(port.name()))
                                .setMode(SubscriptionMode.SAMPLE)
                                .build()).collect(Collectors.toList()))


                .addSubscription(Subscription.newBuilder().setPath(comp)
                .setMode(SubscriptionMode.ON_CHANGE))
                .build();
        */
        //log.info("sub" + subscriptionList.toString());

        /*gnmiController.get(deviceId).subscribe(
                SubscribeRequest.newBuilder()
                        .setSubscribe(subscriptionList)
                        .build());*/

    }

    @Deactivate
    public void deactivate() {

    }

    private Path interfaceOperStatusPath(String interfaceName) {
        return Path.newBuilder()
                .addElem(Gnmi.PathElem.newBuilder().setName("interfaces").build())
                .addElem(Gnmi.PathElem.newBuilder()
                        .setName("interface").putKey("name", "s1-eth1").build())
                //.addElem(Gnmi.PathElem.newBuilder().setName("state").build())
                //.addElem(Gnmi.PathElem.newBuilder().setName("oper-status").build())
                .build();
    }

    private class InnerDeviceListener implements GnmiEventListener {

        @Override
        public void event(GnmiEvent event) {

            log.info("################## GNMI EVENT ##################" + event.toString());
        }

    }
}

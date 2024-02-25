package org.example;

import org.eclipse.paho.client.mqttv3.*;

public class MqttRead {
    private final MqttClient client;

    public MqttRead(MqttClient client) {
        this.client = client;
    }

    public void start(String topic) throws MqttException {
        if (!client.isConnected()) {
            // Intentar reconectar o manejar de acuerdo a la lógica de la aplicación
            System.out.println("Cliente no está conectado, intentando reconectar...");
            // Aquí podrías intentar reconectar o lanzar una excepción
        }

        System.out.println("Suscribiendo al topic: " + topic);
        try {
            // Aquí puedes especificar el nivel de QoS como segundo argumento si es necesario (por ejemplo, 1 para "al menos una vez")
            client.subscribe(topic, (topic1, msg) -> {
                System.out.println("Mensaje recibido en " + topic1 + " : " + new String(msg.getPayload()));
                // Aquí manejas el mensaje recibido
            });
        } catch (MqttException e) {
            System.out.println("Error al suscribirse al topic: " + e.getMessage());
            e.printStackTrace();
            // Manejar la excepción de acuerdo a la lógica de la aplicación
        }
    }
}



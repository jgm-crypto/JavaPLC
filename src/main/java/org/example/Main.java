package org.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {
    public static void main(String[] args) {

        String broker = "tcp://localhost:1883";
        String clientId = "JavaClient";
        String topicPublish = "sensor/data"; // Topic para enviar datos
        String topicSubscribe = "java/request"; // Topic para recibir comandos
        MemoryPersistence persistence = new MemoryPersistence();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Conectando al broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Conectado");

            MqttWrite publisher = new MqttWrite(sampleClient);

            // Conectar y empezar a escuchar en el suscriptor
            sampleClient.subscribe(topicSubscribe, (topic, message) -> {
                JSONObject msg = new JSONObject();
                msg.put("Campo", "Temperatura");
                msg.put("Valor", 26);

                String msgString = msg.toString();

                System.out.println("Intentando enviar mensaje...");
                publisher.publishMessage(topicPublish, msgString);
                System.out.println("Mensaje enviado a: " + topicPublish);
            });

            //Runnable publishTask = getRunnable(sampleClient, topicSubscribe, topicPublish);
            // Programar la tarea para ejecutarse cada 6 segundos
            //executorService.scheduleAtFixedRate(publishTask, 0, 6, TimeUnit.SECONDS);

            // Bloquear el hilo principal para evitar que el programa termine
            try {
                Thread.sleep(Long.MAX_VALUE); // Esto mantiene el programa corriendo
            } catch (InterruptedException e) {
                System.out.println("Hilo principal interrumpido.");
                executorService.shutdownNow(); // Asegurar que el executor se cierre al interrumpir
            }

        } catch (MqttException me) {
            System.out.println("Razón: " + me.getReasonCode());
            System.out.println("Mensaje: " + me.getMessage());
            System.out.println("Localización: " + me.getLocalizedMessage());
            System.out.println("Causa: " + me.getCause());
            me.printStackTrace();
        }
    }

    private static Runnable getRunnable(MqttClient sampleClient, String topicSubscribe, String topicPublish) throws MqttException {
        MqttWrite publisher = new MqttWrite(sampleClient);
        MqttRead subscriber = new MqttRead(sampleClient);

        // Conectar y empezar a escuchar en el suscriptor
        subscriber.start(topicSubscribe);

        JSONObject msg = new JSONObject();
        msg.put("Campo", "Temperatura");
        msg.put("Valor", 26);

        String msgString = msg.toString();

        return () -> {
            if (sampleClient.isConnected()) { // Verificar la conexión antes de publicar
                try {
                    System.out.println("Intentando enviar mensaje...");
                    publisher.publishMessage(topicPublish, msgString);
                    System.out.println("Mensaje enviado a: " + topicPublish);
                } catch (MqttException e) {
                    System.out.println("Error al publicar el mensaje.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("Cliente no conectado, reintentando conexión...");
                // Reintentar la conexión o manejar el error adecuadamente
            }
        };
    }
}
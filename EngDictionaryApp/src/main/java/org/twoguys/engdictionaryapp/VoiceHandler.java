package org.twoguys.engdictionaryapp;

import com.voicerss.tts.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.FileOutputStream;

public class VoiceHandler {
    public static void main (String args[]) throws Exception {
        VoiceProvider tts = new VoiceProvider("b5e4ee6e7b5040c092995d9f3b31054a");

        VoiceParameters params = new VoiceParameters("So Sad", Languages.English_UnitedStates);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);

        tts.addSpeechErrorEventListener(new SpeechErrorEventListener() {
            @Override
            public void handleSpeechErrorEvent(SpeechErrorEvent e) {
                System.out.print(e.getException().getMessage());
            }
        });

        tts.addSpeechDataEventListener(new SpeechDataEventListener() {
            @Override
            public void handleSpeechDataEvent(SpeechDataEvent e) {
                try {
                    byte[] voice = (byte[])e.getData();

                    FileOutputStream fos = new FileOutputStream("word.wav");
                    fos.write(voice, 0, voice.length);
                    fos.flush();
                    fos.close();
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("word.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                    while((clip.isRunning() || clip.isOpen()) && !(clip.isRunning() && !clip.isActive()))
                    {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        tts.speechAsync(params);
    }
}

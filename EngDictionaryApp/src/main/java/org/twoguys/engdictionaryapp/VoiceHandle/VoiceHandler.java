package org.twoguys.engdictionaryapp.VoiceHandle;

import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.*;
import org.twoguys.engdictionaryapp.AlertInfo.AlertInfo;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class VoiceHandler {
    private final static String API_KEY = "b5e4ee6e7b5040c092995d9f3b31054a";
    public final static String US = Languages.English_UnitedStates;
    public final static String UK = Languages.English_GreatBritain;
    String voice = US;

    public void playSound(String word, String language) throws Exception {
        VoiceProvider tts = new VoiceProvider(API_KEY);

        VoiceParameters params = new VoiceParameters(word, voice);
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
                    byte[] voice = (byte[]) e.getData();
                    try {
                        final AudioInputStream ain = AudioSystem.getAudioInputStream(new ByteArrayInputStream(voice));
                        try {
                            final DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat());
                            final Clip clip = (Clip) AudioSystem.getLine(info);
                            clip.open(ain);
                            clip.start();
                        } catch (LineUnavailableException ex) {
                            ex.printStackTrace();
                        } finally {
                            try {
                                ain.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (UnsupportedAudioFileException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        tts.speechAsync(params);
        //tts.speech(params);
    }

    public void setVoice(String voice) {
        if (voice.equals("us"))
            this.voice = US;
        if (voice.equals("uk"))
            this.voice = UK;
    }
}
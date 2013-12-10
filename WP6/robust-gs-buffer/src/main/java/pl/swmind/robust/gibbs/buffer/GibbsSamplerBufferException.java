package pl.swmind.robust.gibbs.buffer;


public class GibbsSamplerBufferException extends Exception{

    public GibbsSamplerBufferException(){
        super();
    }

    public GibbsSamplerBufferException(String message){
        super(message);
    }

    public GibbsSamplerBufferException(Exception ex){
        super(ex);
    }



}

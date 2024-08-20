package xyz.yourboykyle.secretroutes.utils.multiStorage;

public class Triple <X, Y, Z> extends Tuple<X, Y>{
    private Z three;

    public Triple(X one, Y two, Z three){
        super(one, two);
        this.three = three;
    }
    public void setThree(Z three) {this.three = three;}
    public Z getThree() {return three;}
}

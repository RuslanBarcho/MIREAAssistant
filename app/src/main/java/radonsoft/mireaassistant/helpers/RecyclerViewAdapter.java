package radonsoft.mireaassistant.helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import radonsoft.mireaassistant.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    public int day;
    public boolean parity;
    public RecyclerViewAdapter(int day, boolean parity){
        switch(day){
            case 0: this.day = 0;
                break;
            case 1: this.day = day + 5;
                break;
            case 2: this.day = day + 10;
                break;
            case 3: this.day = day + 15;
                break;
            case 4: this.day = day + 20;
                break;
            case 5: this.day = day + 25;
                break;
            default:
                break;
        }
        this.parity = parity;
    }

    @Override
    public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item, null, true);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
            switch(position){
                case 0: {
                    holder.beginTime.setText("9:00");
                    holder.beginTime.setText("10:30");
                }
                break;
                case 1: {
                    holder.beginTime.setText("10:40");
                    holder.beginTime.setText("12:10");
                }
                break;
            }
            set(holder.subjName, holder.classroom, holder.professor, position);
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    private void set(TextView view1,TextView view2,TextView view3, int position){
        if (parity){
            view1.setText(Global.scheduleNamesEvenString[position + day]);
            view2.setText(Global.scheduleRoomsEvenString[position + day]);
            view3.setText(Global.scheduleTeachersEvenString[position + day]);
        } else {
            view1.setText(Global.scheduleNamesOddString[position + day]);
            view2.setText(Global.scheduleRoomsOddString[position + day]);
            view3.setText(Global.scheduleTeachersOddString[position + day]);
        }
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView beginTime;
        public TextView endTime;
        public TextView subjName;
        public TextView classroom;
        public TextView professor;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            beginTime = itemView.findViewById(R.id.item_begintime);
            endTime = itemView.findViewById(R.id.item_endtime);
            subjName = itemView.findViewById(R.id.item_name);
            classroom = itemView.findViewById(R.id.item_class);
            professor = itemView.findViewById(R.id.item_professor);
        }

    }

}

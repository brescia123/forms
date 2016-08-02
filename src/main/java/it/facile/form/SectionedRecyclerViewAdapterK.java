package it.facile.form;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.facile.form.viewmodel.SectionViewModel;

public class SectionedRecyclerViewAdapterK extends RecyclerView.Adapter {

    private static final int SECTION_HEADER_VIEW_TYPE = 0;
    private static final int SECTION_FIRST_HEADER_VIEW_TYPE = 1;

    private boolean valid = true;
    private int sectionLayout;
    private Integer sectionFirstLayout = null;
    private SparseArray<SectionViewModel> sections = new SparseArray<>();
    private RecyclerView.Adapter adapter;

    public SectionedRecyclerViewAdapterK(@LayoutRes int sectionLayout) {
        this.sectionLayout = sectionLayout;
    }

    public SectionedRecyclerViewAdapterK(@LayoutRes int sectionLayout,
                                         @LayoutRes int sectionFirstLayout) {
        this.sectionLayout = sectionLayout;
        this.sectionFirstLayout = sectionFirstLayout;
    }

    public void setAdapter(final RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        this.adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                valid = adapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                valid = adapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                valid = adapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                valid = adapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
        notifyDataSetChanged();
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_HEADER_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(sectionLayout, parent, false);
            return new SectionViewHolder(v, R.id.title);
        } else if (viewType == SECTION_FIRST_HEADER_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    sectionFirstLayout != null ? sectionFirstLayout : sectionLayout, parent, false);
            return new SectionViewHolder(v, R.id.title);
        } else {
            return adapter.onCreateViewHolder(parent, viewType - 1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder) holder).bind(sections.get(position));
        } else {
            adapter.onBindViewHolder(holder, sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return valid
                ? adapter.getItemCount() + sections.size()
                : 0;
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - sections.indexOfKey(position)
                : adapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? position == 0 ? SECTION_FIRST_HEADER_VIEW_TYPE : SECTION_HEADER_VIEW_TYPE
                : adapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.valueAt(i).getFirstPosition() > position) {
                break;
            }
            offset++;
        }

        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (sections.valueAt(i).getSectionedPosition() > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return sections.get(position) != null;
    }

    public void setSections(SectionViewModel[] newSectionViewModels) {
        sections.clear();
        for (SectionViewModel sectionViewModel : newSectionViewModels) {
            sections.append(sectionViewModel.getSectionedPosition(), sectionViewModel);
        }
        notifyDataSetChanged();
    }

    private class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public SectionViewHolder(View itemView, int titleResourceId) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(titleResourceId);
        }

        public void bind(SectionViewModel sectionViewModel) {
            titleTextView.setText(sectionViewModel.getTitle());
        }
    }
}

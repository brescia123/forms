package it.facile.form;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.facile.form.ui.WithOriginalHeight;

public class SectionedRecyclerViewAdapter extends RecyclerView.Adapter implements WithOriginalHeight {

    private static final int SECTION_HEADER_VIEW_TYPE = 0;
    private static final int SECTION_FIRST_HEADER_VIEW_TYPE = 1;

    private boolean valid = true;
    private int sectionLayout;
    private int originalHeight;
    private Integer sectionFirstLayout = null;
    private SparseArray<PositionAwareSectionViewModel> awareSections = new SparseArray<>();
    private RecyclerView.Adapter adapter;

    public SectionedRecyclerViewAdapter(@LayoutRes int sectionLayout,
                                        @LayoutRes Integer sectionFirstLayout) {
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
            return new SectionViewHolder(v, R.id.sectionTitle);
        } else if (viewType == SECTION_FIRST_HEADER_VIEW_TYPE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    sectionFirstLayout != null ? sectionFirstLayout : sectionLayout, parent, false);
            return new SectionViewHolder(v, R.id.sectionTitle);
        } else {
            return adapter.onCreateViewHolder(parent, viewType - 1);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder) holder).bind(awareSections.get(position));
        } else {
            adapter.onBindViewHolder(holder, sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemCount() {
        return valid
                ? adapter.getItemCount() + awareSections.size()
                : 0;
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - awareSections.indexOfKey(position)
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
        for (int i = 0; i < awareSections.size(); i++) {
            if (awareSections.valueAt(i).getFirstPosition() > position) {
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
        for (int i = 0; i < awareSections.size(); i++) {
            if (awareSections.valueAt(i).getSectionedPosition() > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return awareSections.get(position) != null;
    }

    public void setAwareSections(PositionAwareSectionViewModel[] newSectionViewModels) {
        awareSections.clear();
        for (PositionAwareSectionViewModel sectionViewModel : newSectionViewModels) {
            awareSections.append(sectionViewModel.getSectionedPosition(), sectionViewModel);
        }
        notifyDataSetChanged();
    }

    public SparseArray<PositionAwareSectionViewModel> getAwareSections() {
        return awareSections;
    }

    public void setAwareSection(PositionAwareSectionViewModel sectionViewModel) {
        awareSections.put(sectionViewModel.getSectionedPosition(), sectionViewModel);
    }

    @Override
    public int getOriginalHeight() {
        return originalHeight;
    }

    @Override
    public void setOriginalHeight(int i) {
        originalHeight = i;
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;

        public SectionViewHolder(View itemView, int titleResourceId) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(titleResourceId);
            setOriginalHeight(itemView.getLayoutParams().height);
        }

        public void bind(PositionAwareSectionViewModel sectionViewModel) {
            titleTextView.setText(sectionViewModel.getTitle());
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (sectionViewModel.isHidden()) {
                param.height = 0;
            } else {
                param.height = getHeight();
            }
            itemView.setLayoutParams(param);
        }

        public int getHeight() {
            return (int) (titleTextView.getText().toString().trim().isEmpty()
                                ? 0
                                : getOriginalHeight());
        }
    }

    public class PositionAwareSectionViewModel {
        private int firstPosition;
        private int sectionedPosition;
        private String title;
        private boolean hidden;

        public PositionAwareSectionViewModel(int firstPosition, int sectionedPosition, String title, boolean hidden) {
            this.firstPosition = firstPosition;
            this.sectionedPosition = sectionedPosition;
            this.title = title;
            this.hidden = hidden;
        }

        public int getFirstPosition() {
            return firstPosition;
        }

        public int getSectionedPosition() {
            return sectionedPosition;
        }

        public String getTitle() {
            return title;
        }

        public boolean isHidden() {
            return hidden;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PositionAwareSectionViewModel that = (PositionAwareSectionViewModel) o;

            if (firstPosition != that.firstPosition) return false;
            if (sectionedPosition != that.sectionedPosition) return false;
            if (hidden != that.hidden) return false;
            return title != null ? title.equals(that.title) : that.title == null;

        }

        @Override
        public int hashCode() {
            int result = firstPosition;
            result = 31 * result + sectionedPosition;
            result = 31 * result + (title != null ? title.hashCode() : 0);
            result = 31 * result + (hidden ? 1 : 0);
            return result;
        }
    }
}

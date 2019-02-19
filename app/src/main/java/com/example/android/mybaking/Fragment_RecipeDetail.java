package com.example.android.mybaking;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_RecipeDetail extends Fragment implements Adapter_RecipeStep.ListItemClickListener {

    @BindView(R.id.ingredients_rv)
    RecyclerView ingredients_rv;

    @BindView(R.id.steps_rv)
    RecyclerView steps_rv;

    List<Ingredient> ingredients;
    Adapter_Ingredient ingredientAdapter;

    ArrayList<RecipeStep> steps;
    Adapter_RecipeStep stepAdapter;

    boolean isTwoPane;

    int mClickedItemIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        ButterKnife.bind(this,rootView);

        Recipe recipe = getArguments().getParcelable("recipe");
        isTwoPane = getArguments().getBoolean("isTwoPane");
        mClickedItemIndex = getArguments().getInt("mClickedItemIndex");

        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(getContext());

        ingredients=recipe.getIngredients();

        ingredientAdapter = new Adapter_Ingredient(ingredients);
        ingredientAdapter.notifyDataSetChanged();

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());
        ingredients_rv.setLayoutManager(mLayoutManager1);
        ingredients_rv.setAdapter(ingredientAdapter);

        steps=recipe.getSteps();

        stepAdapter = new Adapter_RecipeStep(steps,isTwoPane,this, mClickedItemIndex);
        stepAdapter.notifyDataSetChanged();

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());

        steps_rv.setLayoutManager(mLayoutManager2);
        steps_rv.setAdapter(stepAdapter);

        //when the index > 0, we force the load of the recipe detail
        if(mClickedItemIndex > 0){
            init(mClickedItemIndex);
        }

        return rootView;
    }

    private void init(int clickedItemIndex){
        if(isTwoPane){

            Bundle bundle = new Bundle();
            bundle.putInt("index",clickedItemIndex);

            if(clickedItemIndex==0){
                bundle.putString("step_title",steps.get(clickedItemIndex).getShortDescription());
            }
            else{
                bundle.putString("step_title","Step #"+Integer.toString(clickedItemIndex));
            }

            bundle.putBoolean("isTwoPane",isTwoPane);
            bundle.putParcelableArrayList("steps",steps);

            Fragment_StepDescription stepDescriptionFrag = new Fragment_StepDescription();
            stepDescriptionFrag.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.recipe_step_frame, stepDescriptionFrag)
                    .commit();
        }
        else{
            Intent intent = new Intent(getActivity(), Activity_StepDescription.class);
            intent.putExtra("index",clickedItemIndex);

            if(clickedItemIndex==0){
                intent.putExtra("step_title",steps.get(clickedItemIndex).getShortDescription());
            }
            else{
                intent.putExtra("step_title","Step #"+Integer.toString(clickedItemIndex));
            }

            intent.putExtra("isTwoPane",isTwoPane);
            intent.putParcelableArrayListExtra("steps",steps);
            startActivity(intent );
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex){
        mClickedItemIndex = clickedItemIndex;
        init(mClickedItemIndex);
        ((OnStepClickListener) getActivity()).onClick(mClickedItemIndex);
    }
}
package com.tomatodev.timerdroid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.tomatodev.timerdroid.CategoriesCursorAdapter;
import com.tomatodev.timerdroid.R;
import com.tomatodev.timerdroid.activities.ListTimersActivity;
import com.tomatodev.timerdroid.persistence.CategoriesProvider;
import com.tomatodev.timerdroid.persistence.CategoriesProvider.CategoriesTable;

public class CategoriesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final int SELECT_IMAGE = 0;

	private CursorAdapter items;
	
	private Uri imageUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fillData();
	}

	private void fillData() {
		getLoaderManager().initLoader(0, null, this);
		items = new CategoriesCursorAdapter(this.getActivity(), null);
		setListAdapter(items);

		ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO
//				FragmentManager fragmentManager = getFragmentManager();
//				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//				Fragment newFragment = new ListCountersFragment();
//				Bundle b = new Bundle();
//				b.putInt("category_id", items.getCursor().getInt(0));
//				newFragment.setArguments(b);
//				
//				fragmentTransaction.add(R.id.fragment_container, newFragment);
//				fragmentTransaction.hide(CategoriesFragment.this);
//				fragmentTransaction.addToBackStack(null);
//				// Other transits first hid the categories, displayed them again & then hid again
//				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
//
//				fragmentTransaction.commit();
				
				Intent intent = new Intent(getActivity().getApplicationContext(), ListTimersActivity.class);
				
//				intent.getExtras().putInt("category_id", items.getCursor().getInt(0));
				intent.putExtra("category_id", items.getCursor().getInt(0));
				startActivity(intent);

			}
		});

		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, int arg2,
					final long arg3) {

				// Show dialog to choose edit or delete
				final CharSequence[] actions = { getString(R.string.categories_action_edit),
						getString(R.string.categories_action_delete) };
				AlertDialog.Builder builder = new AlertDialog.Builder(arg1.getContext());
				builder.setTitle(getString(R.string.list_timers_choose_action));
				builder.setItems(actions, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int action) {
						switch (action) {
						case 0:
							final Dialog editCategoryDialog = createChangeCategoryDialog(items,
									arg1, arg3);
							editCategoryDialog.show();
							break;
						case 1:
							AlertDialog.Builder builder = new AlertDialog.Builder(
									CategoriesFragment.this.getActivity());
							builder.setMessage(
									getString(R.string.categories_deletedialog_title_1) + " "
											+ getString(R.string.categories_deletedialog_title_2))
									.setCancelable(false).setPositiveButton(
											getString(R.string.main_deletedialog_yes),
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													getActivity()
															.getContentResolver()
															.delete(Uri
																	.parse(CategoriesTable.CONTENT_ID_URI_BASE
																			+ "/"
																			+ Long.toString(items
																					.getCursor()
																					.getInt(0))),
																	null, null);
													fillData();
												}
											}).setNegativeButton(
											getString(R.string.main_deletedialog_no),
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog, int id) {
													dialog.cancel();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
							break;
						default:
							break;
						}

					}
				});
				AlertDialog alert = builder.create();
				alert.show();

				return true;
			}

			private Dialog createChangeCategoryDialog(final CursorAdapter items, View arg1,
					final long arg3) {
				// Create the dialog
				Context mContext = arg1.getContext();
				final Dialog dialog = new Dialog(mContext);
				dialog.setContentView(R.layout.rename_category_dialog);
				dialog.setTitle(getString(R.string.categories_rename_category));
				dialog.setOwnerActivity(CategoriesFragment.this.getActivity());

				final EditText categoryName = (EditText) dialog
						.findViewById(R.id.rename_category_inputfield);
				categoryName.setText(items.getCursor().getString(1));

				// working, but doesn't paint the new image if one is chosen,
				// therefore commented out
				// ImageView img = (ImageView)
				// dialog.findViewById(R.id.rename_category_image);
				// String currentImageUri = items.getCursor().getString(3);
				// if (currentImageUri != null){
				// img.setImageBitmap(Utilities.decodeFile(mContext,
				// currentImageUri));
				// }

				Button imagePickerButton = (Button) dialog
						.findViewById(R.id.rename_category_chooseimage_button);
				imagePickerButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivityForResult(new Intent(Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
								SELECT_IMAGE);
					}

				});

				Button okButton = (Button) dialog.findViewById(R.id.rename_category_okbutton);
				okButton.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (imageUri != null) {
							ContentValues values = new ContentValues();
							values.put(CategoriesTable.CATEGORIES_KEY_NAME, categoryName.getText()
									.toString());
							values.put(CategoriesTable.CATEGORIES_KEY_PARENT_CATEGORY, items
									.getCursor().getInt(2));
							values.put(CategoriesTable.CATEGORIES_KEY_IMAGE, imageUri.toString());
							getActivity().getContentResolver().update(
									Uri.parse(CategoriesTable.CONTENT_ID_URI_BASE + "/"
											+ Long.toString(arg3)), values, null, null);
						} else {
							ContentValues values = new ContentValues();
							values.put(CategoriesTable.CATEGORIES_KEY_NAME, categoryName.getText()
									.toString());
							values.put(CategoriesTable.CATEGORIES_KEY_PARENT_CATEGORY, items
									.getCursor().getInt(2));
							getActivity().getContentResolver().update(
									Uri.parse(CategoriesTable.CONTENT_ID_URI_BASE + "/"
											+ Long.toString(arg3)), values, null, null);
						}

						dialog.dismiss();
						imageUri = null;
						fillData();
					}
				});
				return dialog;
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE)
		    if (resultCode == AppCompatActivity.RESULT_OK) {
		      imageUri = data.getData();
		    } 
	}

	static final String[] PROJECTION = new String[] { CategoriesProvider.CategoriesTable._ID,
			CategoriesProvider.CategoriesTable.CATEGORIES_KEY_NAME,
			CategoriesProvider.CategoriesTable.CATEGORIES_KEY_PARENT_CATEGORY,
			CategoriesProvider.CategoriesTable.CATEGORIES_KEY_IMAGE, };
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cl = new CursorLoader(getActivity(),
				CategoriesProvider.CategoriesTable.CONTENT_URI, PROJECTION, null, null, null);
		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		items.swapCursor(arg1);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		items.swapCursor(null);
	}
	
}

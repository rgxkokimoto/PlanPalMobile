package com.example.planpalmobile.ui.eventmanager.editevent;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.ActivityEventoDetalleBinding;
import com.example.planpalmobile.ui.eventmanager.comon.EventManagerViewModel;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

public class EventoDetalleActivity extends Fragment {

    private ActivityEventoDetalleBinding binding;
    private final SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private EventManagerViewModel eMvm;
    private String eventoId;
    private final List<Date> dateList = new ArrayList<>();
    private FlexboxLayout flexboxLayout;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm", Locale.getDefault());


    @Override
    public View onCreateView(@NonNull LayoutInflater name, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityEventoDetalleBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        eMvm = new ViewModelProvider(requireActivity()).get(EventManagerViewModel.class);
        flexboxLayout = binding.flexboxChips;
        setData();

        binding.btnVolver.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        binding.btnCategoria.setOnClickListener(v -> {
            eMvm.putNewCategory(requireContext(), etiqueta -> {
                Map<String, Object> data = new HashMap<>();
                data.put("etiqueta", etiqueta);
                saveNewFieldResponse(data, updated -> {
                    if (updated) {
                        binding.btnCategoria.setText(etiqueta);
                        Toast.makeText(requireContext(), "Etiqueta actualizada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar el campo etiqueta", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });


        binding.btnCodigo.setOnClickListener(v -> {
            setNewTitle();
        });

        binding.btnEditarDescripcion.setOnClickListener(v -> {

            eMvm.putNewDesc(requireContext(), newDesc -> {

                if (newDesc != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("descripcion", newDesc);
                    saveNewFieldResponse(data, updated -> {
                        if (updated) {
                            binding.tvDescription.setText(newDesc);
                            Toast.makeText(requireContext(), "Descripción actualizada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el campo descripcion", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar el campo descripcion", Toast.LENGTH_SHORT).show();
                }

            });
        });

        binding.btnPickDay.setOnClickListener(v -> {
            blanckButtons(R.color.white);
            eMvm.showDatePicker(requireContext(), binding.btnPickDay, binding.btnPickDayEn, () -> {
                updateDataButtons();
                eMvm.validateNewDateUpdated(binding.btnPickDay, binding.btnPickDayEn, resp -> {
                    Toast.makeText(requireContext(), resp, Toast.LENGTH_SHORT).show();
                }) ;
            });
        });

        binding.btnPickTime.setOnClickListener(v -> {
            blanckButtons(R.color.white);
            eMvm.showTimePicker(requireContext(), binding.btnPickTime, () -> {
                updateDataButtons();
                eMvm.validateNewDateUpdated(binding.btnPickTime, null , resp -> {
                    Toast.makeText(requireContext(), resp, Toast.LENGTH_SHORT).show();
                });
            });
        });

        binding.btnPickDayEn.setOnClickListener(v -> {
            blanckButtons(R.color.white);
            eMvm.showDatePicker(requireContext(), binding.btnPickDayEn, null, () -> {
                eMvm.validateNewDateUpdated(binding.btnPickDayEn, binding.btnPickTimeEnd , resp -> {
                    updateDataButtons();
                    Toast.makeText(requireContext(), resp, Toast.LENGTH_SHORT).show();
                });
            });
        });

        binding.btnPickTimeEnd.setOnClickListener(v -> {
            blanckButtons(R.color.white);
            eMvm.showTimePicker(requireContext(), binding.btnPickTimeEnd , () -> {
                updateDataButtons();
                eMvm.validateNewDateUpdated(binding.btnPickTimeEnd, binding.btnPickDayEn, resp -> {
                    Toast.makeText(requireContext(), resp, Toast.LENGTH_SHORT).show();
                });
            });
        });

        binding.btnNewDate.setOnClickListener(v -> createNewMeet());

        binding.tvToggleDesc.setOnClickListener(v -> toggleDescripcion());

        return root;
    }

    private void updateDataButtons() {
        eMvm.setInitDay(binding.btnPickDay);
        eMvm.setInitTime(binding.btnPickTime);
        eMvm.setEndDay(binding.btnPickDayEn);
        eMvm.setEndTime(binding.btnPickTimeEnd);
    }

    private void setNewTitle() {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(binding.btnCodigo.getText());
        new AlertDialog.Builder(requireContext())
                .setTitle("Introduce una descripción")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {


                    Map<String, Object> data = new HashMap<>();
                    data.put("codigo", input.getText().toString());
                    saveNewFieldResponse(data, updated -> {
                        if (updated) {
                            binding.btnCodigo.setText(input.getText().toString());
                            eMvm.setTitle(input.getText().toString());
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el campo titulo", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void saveNewFieldResponse(Map<String, Object> data, Consumer<Boolean> onUpdate) {

        eMvm.updateFieldEvent(eventoId, data, resp -> {
            if ("OK".equals(resp)) {
                onUpdate.accept(true);
            } else {
                onUpdate.accept(false);
            }
        });

    }

    private void setData() {
        Bundle args = getArguments();
        if (args != null) {
            String codigo = args.getString("codigo");
            String descripcion = args.getString("descripcion");
            long inicioMillis = args.getLong("horaInicio", 0);
            long finMillis = args.getLong("horaFin", 0);
            Date inicio = new Date(inicioMillis);
            Date fin = new Date(finMillis);
            String etiqueta = args.getString("etiqueta");
            eventoId = args.getString("id");
            long[] horasDisponiblesArray = getArguments().getLongArray("horasDisponibles");
            Log.d("FHD", "horasDisponiblesArray: " + horasDisponiblesArray.length);
            if (horasDisponiblesArray != null) {
                for (long millis : horasDisponiblesArray) {
                    dateList.add(new Date(millis));
                }
            }

            eMvm.setEventoId(eventoId);
            eMvm.setEtiquetas(etiqueta);
            eMvm.setTitle(codigo);
            eMvm.setStartDate(inicio);
            eMvm.setEndDate(fin);
            eMvm.setDescription(descripcion);
            eMvm.setHorasDisponibles(dateList);


            binding.btnCategoria.setText(etiqueta);
            binding.btnCodigo.setText((codigo != null ? codigo : "Sin título"));
            binding.btnPickDay.setText(sdfFecha.format(inicio));
            binding.btnPickTime.setText(sdfHora.format(inicio));
            binding.btnPickDayEn.setText(sdfFecha.format(fin));
            binding.btnPickTimeEnd.setText(sdfHora.format(fin));
            updateDataButtons();
            binding.tvDescription.setText((descripcion != null ? descripcion : "Sin descripción"));
            updateChipGroup();
        }
    }

    private void createNewMeet() {

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            try {

                String dateStr = binding.btnPickDay.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(dateStr);

                Calendar calendar = Calendar.getInstance();
                if (date != null) {
                    calendar.setTime(date);
                }

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute1);

                Date newDate = calendar.getTime();
                Log.d("CreateEventDetailFragment", "NewDateMeet: " + newDate);

                Date evetInitDate = eMvm.parseDateTimeFromButtons(binding.btnPickDay, binding.btnPickTime);
                Date eventEndEnd = eMvm.parseDateTimeFromButtons(binding.btnPickDayEn, binding.btnPickTimeEnd);
                Log.d("CreateEventDetailFragment", "DateInMeet: " + evetInitDate);
                Log.d("CreateEventDetailFragment", "DateEndMeet: " + eventEndEnd);

                boolean isNewMeetValid = eMvm.validateNewMeet(newDate, dateList, evetInitDate, eventEndEnd);

                if (!isNewMeetValid) {
                    Toast.makeText(requireContext(), "Fecha invalida, fuera del evento oh repetida", Toast.LENGTH_SHORT).show();
                    return;
                }

                dateList.add(newDate);
                Log.d("CreateEventDetailFragment", "createNewMeet: " + dateList);
                updateChipGroup();
                updateFirestoreHorasDisponibles();

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Formato de meet incorrecto", Toast.LENGTH_SHORT).show();
            }

        }, hour, minute, true).show();
    }

    private void updateChipGroup() {
        if (flexboxLayout.getChildCount() > 0) {
            flexboxLayout.removeAllViews();
        }
        for (int i = 0; i < dateList.size(); i++) {
            Date date = dateList.get(i);
            Chip chip = new Chip(requireContext());
            chip.setText(SDF.format(date));
            chip.setCloseIconVisible(true);
            int finalI = i;
            chip.setOnCloseIconClickListener(v -> {
                dateList.remove(finalI);
                Log.d("CreateEventDetailFragment", "updateChipGroup: " + dateList);
                updateChipGroup();

                SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                sformat.setTimeZone(TimeZone.getTimeZone("UTC"));

                List<String> horasDisponiblesStr = new ArrayList<>();
                for (Date dateUp : dateList) {
                    horasDisponiblesStr.add(sformat.format(dateUp));
                }
                Map<String, Object> data = new HashMap<>();
                data.put("horasDisponibles", horasDisponiblesStr);

                eMvm.updateFieldEvent(eventoId, data, resp -> {
                    if (!"OK".equals(resp)) {
                        Toast.makeText(requireContext(), "Error al actualizar Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            flexboxLayout.addView(chip);
        }
    }

    private void updateFirestoreHorasDisponibles() {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sformat.setTimeZone(TimeZone.getTimeZone("UTC"));

        List<String> horasDisponiblesStr = new ArrayList<>();
        for (Date dateUp : dateList) {
            horasDisponiblesStr.add(sformat.format(dateUp));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("horasDisponibles", horasDisponiblesStr);

        eMvm.updateFieldEvent(eventoId, data, resp -> {
            if (!"OK".equals(resp)) {
                Toast.makeText(requireContext(), "Error al actualizar Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleDescripcion() {
        int maxLines = binding.tvDescription.getMaxLines();
        if (maxLines == 3) {
            binding.tvDescription.setMaxLines(Integer.MAX_VALUE);
            binding.tvToggleDesc.setText("Ver menos");
        } else {
            binding.tvDescription.setMaxLines(3);
            binding.tvToggleDesc.setText("Ver más");
        }
    }

    private void blanckButtons(int blank_background) {
        binding.btnPickDay.setBackgroundColor(getResources().getColor(blank_background));
        binding.btnPickTime.setBackgroundColor(getResources().getColor(blank_background));
        binding.btnPickDayEn.setBackgroundColor(getResources().getColor(blank_background));
        binding.btnPickTimeEnd.setBackgroundColor(getResources().getColor(blank_background));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        eMvm.clearMutableData();
    }

}

package com.mrgautam.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.mrgautam.model.Song;
import com.mrgautam.player.AudioPlayer;
import com.mrgautam.service.SongService;

import javafx.util.Duration;

public class MusicPlayerUI extends JFrame {

    private JLabel nowPlayingLabel;
    private DefaultListModel<Song> listModel;
    private JList<Song> songList;
    private AudioPlayer player;
    private List<Song> songs;
    private List<Song> searchedSongs;
    JLabel playPauseLabel = new JLabel("▶️");
    JLabel nextLabel = new JLabel("⏭️");
    JLabel prevLabel = new JLabel("⏮️");
    private JLabel shuffleLabel = new JLabel("🔀");
    private JLabel repeatLabel = new JLabel("🔁");
    private int currentIndex = -1;
    boolean[] isVolumeVisible = {false};
    boolean[] isPlaying = {false};
    private JSlider seekSlider = new JSlider(0, 100, 0);
    private Timer seekTimer;
    private boolean isSeeking = false;
    private JLabel currentTimeLabel = new JLabel("00:00");
    private JLabel totalTimeLabel = new JLabel("00:00");
    private boolean isShuffleEnabled = false;
    private boolean isRepeatEnabled = false;
    private List<Song> queue = new LinkedList<>();
    private JTextField searchField;
    private JPanel bottomPanel = new JPanel();
    private JPanel searchResultsPanel;
    private JLabel addSongLabel = new JLabel("➕ Add Song");
    private JLabel downloadSongLabel = new JLabel(" ➕ Yt Link Download");

    private SongService service = new SongService();
    private CardLayout centerLayout;
    private JPanel centerpanel;
    private RotatingWheelPanel rotatingWheePanel = new RotatingWheelPanel();
    private List<Song> prevList = new ArrayList<>();

    private Boolean isSearched = false;
    private Boolean isPrev = false;

    public MusicPlayerUI() {
        // Initialize JavaFX
        new javafx.embed.swing.JFXPanel();
        setTitle("🎵 fake spotify");
        setSize(1924, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        songs = service.getAllSongs();
        searchedSongs = service.getAllSongs();
        listModel = new DefaultListModel<>();
        for (Song song : searchedSongs) {
            listModel.addElement(song);
        }

        player = new AudioPlayer();

        // rotatingWheePanel.setPreferredSize(new Dimension(300,300)); // or 250x250
        JPanel wheelWrapper = new JPanel();

        centerLayout = new CardLayout();
        wheelWrapper.setOpaque(false); // Transparent if needed
        wheelWrapper.add(rotatingWheePanel, BorderLayout.CENTER);
        wheelWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // No border
        wheelWrapper.setOpaque(true);           // Ensure background is visible
        wheelWrapper.setBackground(new Color(30, 30, 30, (int) (1.0f * 255))); // Semi-transparent background
        wheelWrapper.repaint();                 // Force immediate redraw
        wheelWrapper.revalidate();             // Update layout (if needed)

        centerpanel = new JPanel(centerLayout);
        centerpanel.add(wheelWrapper, "wheel");
// -----------------------------------------------------------------------------------------------------------------------------

        // for search field
        searchField = new RoundedTextField(30); // Was 20, now 30 columns wide
        searchField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font size
        searchField.setPreferredSize(new Dimension(300, 40)); // Increase height too
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                new EmptyBorder(5, 10, 5, 10) // Padding inside the border
        ));

        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // padding inside text field
        ));
        searchField.setText("Search Songs...");
        searchField.setForeground(Color.GRAY); // Lighter text color

        searchField.setBackground(new Color(40, 40, 40)); // Dark background
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

                isShuffleEnabled = false; // Disable shuffle when searching
                shuffleLabel.setForeground(Color.WHITE); // Reset shuffle label color
                // centerLayout.show(centerpanel, "search");
                if (searchField.getText().equals("Search Songs...")) {

                    isSearched = true;

                    searchField.setText("");
                    searchField.setForeground(Color.WHITE);
                    filtersearchedSongs();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    centerLayout.show(centerpanel, "wheel");
                    isSearched = false;
                    searchedSongs.clear();
                    currentIndex = songs.isEmpty() ? -1 : 0;
                    searchField.setText("Search Songs...");
                    searchField.setForeground(Color.GRAY);
                    // Optional: refresh JList with full songs
                    updateSongListDisplay(songs);
                }

                SwingUtilities.invokeLater(() -> {
                    if (!songList.hasFocus()) {
                        hideSearchPanel();
                    }
                });
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtersearchedSongs();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtersearchedSongs();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtersearchedSongs();
            }
        });

        //    -------------------------------------------------------------------------------------
        // Add a mouse listener to the search field to trigger the search when clicked
        searchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchField.requestFocusInWindow(); // Focus on the search field
                filtersearchedSongs(); // Trigger search on click
            }
            // @Override
            // public void mouseEntered(MouseEvent e) {
            //     filtersearchedSongs();
            //     showSearchPanel();

            // }
        });

        // -- songlist by search field
        songList = new JList<>(listModel);
        songList.setBackground(new Color(40, 40, 40));
        songList.setForeground(Color.WHITE);
        songList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songList.setFixedCellHeight(30);
        songList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // for song labels
        songList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? new Color(60, 60, 60) : new Color(30, 30, 30));
                label.setForeground(isSelected ? Color.green : Color.white);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

// -----------------------------------------------------------------------------------------------------------------------------
        // for add to queue popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addToQueueItem = new JMenuItem("➕ Add to Queue");
        addToQueueItem.setBackground(new Color(40, 40, 40));
        addToQueueItem.setForeground(Color.WHITE);
        popupMenu.add(addToQueueItem);
        songList.setComponentPopupMenu(popupMenu);

        // ----------------------------------------------------------------------------------
        // ----dummy pannel to remove the focus
        JPanel dummyPanel = new JPanel();
        bottomPanel.setFocusable(true);

        SwingUtilities.invokeLater(() -> {
            bottomPanel.requestFocusInWindow(); // or mainPanel.requestFocusInWindow();
        });
        // ----------------------------------------------------------------------------------

        JSlider volumeSlider = new JSlider(0, 100, 100);
        JButton volumeButton = new JButton("🔊");

        nowPlayingLabel = new JLabel("  ", JLabel.CENTER);
        nowPlayingLabel.setForeground(Color.white);
        nowPlayingLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 14));  // Use a Unicode-friendly font

        volumeSlider.setMajorTickSpacing(10);
        volumeSlider.setPaintTicks(false);
        volumeSlider.setPaintLabels(false);

        // --------------------- Play/Pause Button ---------------------
        playPauseLabel.setPreferredSize(new Dimension(80, 80)); // Set preferred size for the labe
        playPauseLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 40)); // try different fonts
        playPauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playPauseLabel.setForeground(Color.WHITE); // Default color
        playPauseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        playPauseLabel.addMouseListener(new MouseAdapter() {
            List<Song> tempList;

            // Create a copy of the list
            @Override
            public void mouseClicked(MouseEvent e) {
                // if (!isSearched) {
                //     tempList = new ArrayList<>(songs);
                // } else {
                //     tempList = new ArrayList<>(searchedSongs);
                // }

                // Song selectedSong = songList.getSelectedValue();
                // int selected = -1;
                // if (selectedSong == null || searchedSongs.isEmpty()) {
                //     System.out.println("search the song"); // Find in full list
                // }
                // if (selectedSong != null) {
                //     selected = tempList.indexOf(selectedSong); // Find in full list
                //     if (selected != -1) {
                //     }
                // }
                if (player.isPlaying()) {
                    player.pause();
                    nowPlayingLabel.setForeground(Color.white);
                    rotatingWheePanel.stopRotation();

                    playPauseLabel.setText("▶️");
                    playPauseLabel.setForeground(Color.WHITE); // Reset color
                    nowPlayingLabel.setText("⏸️ Paused: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                } else {
                    if (player.isPaused()) {
                        rotatingWheePanel.startRotation();

                        player.resume();
                        nowPlayingLabel.setForeground(Color.white);
                        playPauseLabel.setForeground(Color.GREEN); // Reset color
                        playPauseLabel.setText("⏸️");
                        nowPlayingLabel.setText("🎶 Now Playing: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                    } //else if (currentIndex != -1) {
                    //     playCurrentSong();
                    //     playPauseLabel.setText("⏸️");
                    //     playPauseLabel.setForeground(Color.GREEN); // Reset color
                    // } else if (selected != -1) {
                    //     currentIndex = selected;
                    //     playCurrentSong();
                    //     playPauseLabel.setText("⏸️");
                    //     playPauseLabel.setForeground(Color.GREEN); // Reset color

                    // }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playPauseLabel.setForeground(Color.GREEN); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!player.isPlaying()) {
                    playPauseLabel.setForeground(Color.WHITE); // Reset color
                } // else keep it green
                // else keep it green
            }
        });
        // --------------------- Play/Pause Button ends here ---------------------

        //------------------------
        // Next Button
        nextLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        nextLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 25)); // try different fonts
        nextLabel.setForeground(Color.WHITE); // Default color
        nextLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Auto-fallback: if searchedSongs is empty or cleared, fallback to main list

                // if (searchedSongs == null || searchedSongs.isEmpty()) {
                // isSearched = false;
                searchedSongs.clear();
                isPrev = false; // disable previous
                isShuffleEnabled = true; // auto-enable shuffle
                shuffleLabel.setForeground(Color.GREEN); // reflect UI change
                isRepeatEnabled = false; // disable repeat
                repeatLabel.setForeground(Color.WHITE); // reflect UI change
                currentIndex = (int) (Math.random() * songs.size());
                // } else {
                //     if (isShuffleEnabled) {
                //         currentIndex = (int) (Math.random() * searchedSongs.size());
                //     } else {
                //         currentIndex++;
                //         if (currentIndex >= searchedSongs.size()) {
                //             currentIndex = 0;
                //         }
                //     }
                // }

                playCurrentSong();
                playPauseLabel.setText("⏸️");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nextLabel.setForeground(Color.GREEN); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nextLabel.setForeground(Color.WHITE);
            }
        });

        //    /-------------------next button ends here------------------
        // --------------------- Previous Button ---------------------
        prevLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        prevLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 25)); // try different fonts
        prevLabel.setForeground(Color.WHITE); // Default color
        prevLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        prevLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                player.stop();
                isPrev = true;
                isShuffleEnabled = false;
                isRepeatEnabled = false; // disable repeat

                // Auto-fallback logic if searchedSongs is empty
                // if (searchedSongs == null || searchedSongs.isEmpty()) {
                // isSearched = false;
                repeatLabel.setForeground(Color.WHITE); // reflect UI change
                shuffleLabel.setForeground(Color.WHITE); // reflect UI change
                if (prevList.isEmpty()) {
                    // isPrev = false; // disable previous
                    // isShuffleEnabled = true;
                    // shuffleLabel.setForeground(Color.GREEN);
                    // currentIndex = (int) (Math.random() * songs.size());
                    player.stop();
                    rotatingWheePanel.stopRotation();
                    rotatingWheePanel.setVisible(false);
                    nowPlayingLabel.setVisible(isPrev);
                    nowPlayingLabel.setText("⏸️ No previous song found.");
                    nowPlayingLabel.setForeground(Color.RED);
                    playPauseLabel.setText("▶️");
                } else {
                    currentIndex = prevList.size() - 1;
                    playCurrentSong();
                    playPauseLabel.setText("⏸️");
                }                // } else {
                //     currentIndex--;
                //     if (currentIndex < 0) {
                //         currentIndex = searchedSongs.size() - 1;
                //     }
                // }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                prevLabel.setForeground(Color.GREEN); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                prevLabel.setForeground(Color.WHITE);
            }
        });

        // 
        // 
        // 
        seekSlider = new JSlider(0, 100, 0);
        seekSlider.setPreferredSize(new Dimension(400, 20)); // Custom height
        seekSlider.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        seekSlider.setUI(new RoundedSliderUI(seekSlider));
        seekSlider.setPreferredSize(new Dimension(400, 40));
        seekSlider.setEnabled(false); // Disabled until a song plays
        seekSlider.setValue(0);

        seekSlider.addChangeListener(e -> {
            if (!seekSlider.getValueIsAdjusting() && player.isSeekable()) {
                double percent = seekSlider.getValue() / 100.0;
                player.seekTo(percent);
            }
        });
        seekSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                isSeeking = true; // Pause updates while dragging
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                isSeeking = false;
                if (player.isSeekable()) {
                    double percent = seekSlider.getValue() / 100.0;
                    player.seekTo(percent); // Seek only after releasing
                }
            }
        });

        seekSlider.addChangeListener(e -> {
            if (isSeeking && player.isSeekable()) {
                // Optional: visually update UI while dragging if needed
            }
        });

        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            double volume = value / 100.0;
            player.setVolume(volume);
        });
        volumeSlider.setVisible(false); // hidden by default

        volumeButton.addActionListener(e -> {
            isVolumeVisible[0] = !isVolumeVisible[0];
            volumeSlider.setVisible(isVolumeVisible[0]);
            revalidate();
            repaint();
        });

        // ------experiment - shuffleLabel and Repeatlabel-----------------------------------
        shuffleLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        shuffleLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 15)); // try different fonts
        shuffleLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        shuffleLabel.setForeground(Color.WHITE); // Default color
        shuffleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        shuffleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                isShuffleEnabled = !isShuffleEnabled;

                if (isShuffleEnabled) {
                    isRepeatEnabled = false;
                    repeatLabel.setForeground(Color.WHITE);
                    shuffleLabel.setForeground(Color.GREEN);

                } else {
                    shuffleLabel.setForeground(Color.WHITE); // Reset on exit
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                shuffleLabel.setForeground(Color.GREEN); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isShuffleEnabled) {
                    shuffleLabel.setForeground(Color.WHITE);
                } // else keep it green
            }
        });

        // repeat label
        repeatLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        repeatLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 15)); // try different fonts
        repeatLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        repeatLabel.setForeground(Color.WHITE); // Default color
        repeatLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        repeatLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                isRepeatEnabled = !isRepeatEnabled;

                if (isRepeatEnabled) {
                    isShuffleEnabled = false;
                    shuffleLabel.setForeground(Color.WHITE);
                    repeatLabel.setForeground(Color.GREEN);

                } else {
                    repeatLabel.setForeground(Color.WHITE); // Reset on exit
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repeatLabel.setForeground(Color.GREEN); // Hover effect
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isRepeatEnabled) {
                    repeatLabel.setForeground(Color.WHITE);
                } // else keep it green
            }
        });

        addToQueueItem.addActionListener(e -> {
            Song selectedSong = songList.getSelectedValue(); // safer and cleaner

            if (selectedSong != null) {
                Song currentSong;
                if (isSearched && currentIndex >= 0 && currentIndex < searchedSongs.size()) {
                    currentSong = searchedSongs.get(currentIndex);
                } else if (!isSearched && currentIndex >= 0 && currentIndex < songs.size()) {
                    currentSong = songs.get(currentIndex);
                } else {
                    currentSong = null;
                }

                if (selectedSong != null) {
                    queue.add(selectedSong);
                    nowPlayingLabel.setText("✅ Added to Queue: " + selectedSong.getTitle() + " - " + selectedSong.getArtist());
                    nowPlayingLabel.setForeground(Color.GREEN);
                }
            }
        });
        // ----------------------------------------------------------------------
        //To make the right-click work more naturally (when user right-clicks but doesn’t select first), add this:

        songList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                isSearched = true;
                if (e.getClickCount() == 2) {
                    Song selectedSong = songList.getSelectedValue();
                    if (selectedSong != null) {
                        int indexInsearchedSongs = searchedSongs.indexOf(selectedSong); // Find in full list
                        if (indexInsearchedSongs != -1) {
                            currentIndex = indexInsearchedSongs;
                            playCurrentSong();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = songList.locationToIndex(e.getPoint());
                    songList.setSelectedIndex(index);
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int index = songList.locationToIndex(e.getPoint());
                    songList.setSelectedIndex(index);
                }
            }
        });

        // addsong label ------------------------------------------------------
        addSongLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Step 1: Choose File (MP3)
                FileDialog fileDialog = new FileDialog((java.awt.Frame) null, "Select MP3 File", FileDialog.LOAD);
                fileDialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                fileDialog.setVisible(true);

                String selectedFileName = fileDialog.getFile();
                String selectedDir = fileDialog.getDirectory();

                if (selectedFileName != null) {
                    File selectedFile = new File(selectedDir, selectedFileName);

                    if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".mp3")) {
                        // Step 2: Custom Dark Themed Input Panel
                        JTextField titleField = new RoundedTextField(50);
                        JTextField artistField = new RoundedTextField(50);

                        Font font = new Font("Segoe UI", Font.PLAIN, 14);
                        Color bgColor = new Color(30, 30, 30, (int) (0.9f * 255)); // Semi-transparent black
                        Color fgColor = Color.WHITE;

                        titleField.setBackground(bgColor);
                        titleField.setForeground(fgColor);
                        titleField.setFont(font);
                        titleField.setCaretColor(fgColor);
                        titleField.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                                new EmptyBorder(5, 10, 5, 10) // Padding inside the border
                        ));

                        titleField.setBorder(BorderFactory.createCompoundBorder(
                                titleField.getBorder(),
                                BorderFactory.createEmptyBorder(5, 10, 5, 10) // padding inside text field
                        ));

                        artistField.setBackground(bgColor);
                        artistField.setForeground(fgColor);
                        artistField.setFont(font);
                        artistField.setCaretColor(fgColor);
                        artistField.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(Color.GRAY, 1, true), // `true` makes it rounded
                                new EmptyBorder(5, 10, 5, 10) // Padding inside the border
                        ));

                        artistField.setBorder(BorderFactory.createCompoundBorder(
                                artistField.getBorder(),
                                BorderFactory.createEmptyBorder(5, 10, 5, 10) // padding inside text field
                        ));

                        JLabel titleLabel = new JLabel("Title:");
                        JLabel artistLabel = new JLabel("Artist:");
                        titleLabel.setForeground(fgColor);
                        artistLabel.setForeground(fgColor);

                        JPanel panel = new JPanel(new GridLayout(4, 1));
                        panel.setBackground(bgColor);
                        panel.add(titleLabel);
                        panel.add(titleField);
                        panel.add(artistLabel);
                        panel.add(artistField);

                        UIManager.put("Panel.background", Color.BLACK);
                        UIManager.put("OptionPane.background", bgColor);
                        UIManager.put("OptionPane.messageForeground", fgColor);
                        UIManager.put("Button.background", new Color(50, 50, 50));
                        UIManager.put("Button.foreground", Color.WHITE);
                        UIManager.put("Button.font", font);

                        int inputResult = JOptionPane.showConfirmDialog(
                                null, panel, "Add Song Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
                        );

                        if (inputResult == JOptionPane.OK_OPTION) {
                            String title = titleField.getText().trim();
                            String artist = artistField.getText().trim();

                            if (!title.isEmpty() && !artist.isEmpty()) {
                                Song newSong = new Song(0, title, artist, selectedFile.getAbsolutePath());
                                boolean isSongAdded = service.addSong(newSong);

                                if (isSongAdded) {
                                    searchedSongs = service.getAllSongs();
                                    listModel.clear();
                                    for (Song song : searchedSongs) {
                                        listModel.addElement(song);
                                    }

                                    filtersearchedSongs(); // refresh if searched
                                    nowPlayingLabel.setText("🎉 Added: " + title + " - " + artist);
                                    nowPlayingLabel.setForeground(Color.GREEN);

                                    songList.setModel(listModel);
                                } else {
                                    nowPlayingLabel.setText("❌ Something went wrong with adding the song");
                                    nowPlayingLabel.setForeground(Color.GREEN);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Title and artist must not be empty.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a valid .mp3 file only.", "Invalid File", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                addSongLabel.setForeground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addSongLabel.setForeground(Color.LIGHT_GRAY);

            }
        });

        downloadSongLabel.addMouseListener(new MouseAdapter() {
            // @Override
            // public void mouseClicked(MouseEvent e) {
            //     try {
            //         ProcessBuilder pb = new ProcessBuilder("java", "A:\\SEMESTER 4\\java\\tutorial-broCode\\CallingFromAnotherProgram\\MainRunner.java");
            //         // pb.inheritIO(); // To show the output from OtherProgram
            //         Process process = pb.start();
            //         process.waitFor(); // Wait for the other program to finish
            //     } catch (IOException | InterruptedException ex) {
            //         ex.printStackTrace();
            //     }
            //     // Implement your download logic here
            //     nowPlayingLabel.setText("Downloading song(MP3) from YouTube...");
            //     nowPlayingLabel.setForeground(Color.GREEN);
            // }

            @Override
            public void mouseEntered(MouseEvent e) {
                downloadSongLabel.setForeground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                downloadSongLabel.setForeground(Color.LIGHT_GRAY);
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        searchPanel.setBackground(new Color(0, 0, 0, (int) (1.0f * 255))); // Set transparency using RGBA
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);
        // Create a panel to hold the slider  and control its width and currenttime and totaltime labels
        currentTimeLabel.setForeground(new Color(255, 255, 255, (int) (0))); // Set transparency using RGBA
        currentTimeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalTimeLabel.setForeground(new Color(255, 255, 255, (int) (0))); // Set transparency using RGBA
        totalTimeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        seekSlider.setPreferredSize(new Dimension(500, 7)); // 300px wide

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        sliderPanel.setOpaque(false); // Keep background transparent if needed
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 10));

        // Time Panel (for showing current and total time)
        sliderPanel.add(currentTimeLabel, BorderLayout.WEST);
        sliderPanel.add(seekSlider);
        sliderPanel.add(totalTimeLabel, BorderLayout.EAST);

        JPanel controls = new JPanel();
        // controls.setLayout(new BorderLayout());
        // Button Row (Horizontal)
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Horizontal button layout
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.add(shuffleLabel);
        buttonsPanel.add(prevLabel);
        buttonsPanel.add(playPauseLabel);
        buttonsPanel.add(nextLabel);
        buttonsPanel.add(repeatLabel);

        // buttonsPanel.add(volumeButton);
        // buttonsPanel.add(volumeSlider);
        // Bottom Panel (Vertical stack)
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // Vertical stacking
        bottomPanel.setBackground(Color.BLACK);

        bottomPanel.add(Box.createVerticalStrut(10)); // Spacing
        bottomPanel.add(nowPlayingLabel); // 2. Now Playing above seek bar
        nowPlayingLabel.setAlignmentX(bottomPanel.CENTER_ALIGNMENT);

        bottomPanel.add(buttonsPanel); // First row: buttons

        bottomPanel.add(sliderPanel, CENTER_ALIGNMENT); // Seek slider
        bottomPanel.add(Box.createVerticalStrut(20)); // Spacing

        bottomPanel.add(controls); // Optional controls panel (if any)

        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ----------------------------------------------------------------------------------------------------
        //  for search result pannel
        searchResultsPanel = new JPanel(new BorderLayout());
        searchResultsPanel.setVisible(false);
        searchResultsPanel.setBackground(new Color(30, 30, 30));
        searchResultsPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        searchResultsPanel.add(new JScrollPane(songList), BorderLayout.CENTER);
        searchResultsPanel.setBounds(searchField.getX(), searchField.getY() + searchField.getHeight(), searchField.getWidth(), 120);
        centerpanel.add(searchResultsPanel, "search");

        // -- add song pannel
        addSongLabel.setForeground(Color.LIGHT_GRAY);
        addSongLabel.setFont(new Font("Seoge UI Symbol", Font.PLAIN, 14));
        addSongLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadSongLabel.setForeground(Color.LIGHT_GRAY);
        downloadSongLabel.setFont(new Font("Seoge UI Symbol", Font.PLAIN, 14));
        downloadSongLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        downloadSongLabel.setOpaque(true);
        downloadSongLabel.setBackground(new Color(50, 50, 50));
        downloadSongLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel addSongMethodsContainer = new JPanel(new BorderLayout());
        addSongMethodsContainer.setOpaque(true);
        addSongMethodsContainer.setBackground(new Color(50, 50, 50));
        addSongMethodsContainer.add(addSongLabel, BorderLayout.WEST);
        addSongMethodsContainer.add(downloadSongLabel, BorderLayout.EAST);
        // addSongMethodsContainer(createHorizontalStrut())
        searchResultsPanel.add(addSongMethodsContainer, BorderLayout.SOUTH);

        addSongLabel.setOpaque(true);
        addSongLabel.setBackground(new Color(50, 50, 50));
        addSongLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        // ----------------------------------------------------------------------------------------------------

        //    -------------------------------------------------------------------------------------
        // controls.add(volumePanel, BorderLayout.EAST);
        add(searchPanel, BorderLayout.NORTH);
        add(centerpanel);
        add(controls, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        MouseAdapter globalFocusListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Component clicked = SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
                if (clicked != searchField && clicked != songList) {
                    hideSearchPanel();
                    requestFocusInWindow();    // Remove focus from search
                }
            }
        };

        // Attach to all background components (mainPanel, bottomPanel, etc.)
        bottomPanel.addMouseListener(globalFocusListener);
        searchPanel.addMouseListener(globalFocusListener);
        buttonsPanel.addMouseListener(globalFocusListener);

        // ...add to other panels if needed
        // ---------------------------------------------------------------------------
    }

    // -------------------------------------------METHODS------------------------------------------------------
    private void playCurrentSong() {
        nowPlayingLabel.setForeground(Color.white);
        // centerLayout.show(centerpanel, "wheel");
        rotatingWheePanel.setVisible(true);
        rotatingWheePanel.startRotationAtNewSong();
        playPauseLabel.setForeground(Color.GREEN); // Reset color
        hideSearchPanel();
        List<Song> playList;

        // Decide playlist based on search & shuffle state
        if (isSearched) {
            playList = searchedSongs;
        } else {
            playList = songs;
        }

        // If shuffle is enabled, pick random index from 'songs'
        if (isShuffleEnabled) {
            currentIndex = (int) (Math.random() * songs.size());
            playList = songs; // Always shuffle from full song list
        }


        // Handle empty playlist safely
        if (playList.isEmpty() || currentIndex < 0 || currentIndex >= playList.size()) {
            // if(isPrev){
            //     playList = prevList;
            // } else{
            //     playList = songs;
            // }
            // playList = songs;
            if(isPrev){
                playList = prevList;
            }
            System.out.println(currentIndex);
            System.out.println("No valid song to play.");
            return;
        }

        Song song = playList.get(currentIndex);

        // Priority to queue
        if (!queue.isEmpty() && !isRepeatEnabled && !isPrev) {
            song = queue.remove(0);
            currentIndex = playList.indexOf(song); // Use full list for indexing
        } else if (isPrev) {
            // if (prevList.isEmpty()) {
            // player.stop();
            // rotatingWheePanel.stopRotation();
            // nowPlayingLabel.setVisible(isPrev);
            // nowPlayingLabel.setText("⏸️ No previous song found.");
            // } else {
            song = prevList.remove(prevList.size() - 1); // Remove last played song
            //         }if (service.getSongById(player.getMediaSongId()) == song) {
            // song = prevList.remove(prevList.size() - 1); // Remove last played song
            currentIndex = prevList.indexOf(song); // Use full list for indexing
            // }
        } else {
            song = playList.get(currentIndex);
        }

        nowPlayingLabel.setText("🎶 Now Playing: " + song.getTitle() + " - " + song.getArtist());
        songList.setSelectedIndex(currentIndex);
        isPlaying[0] = true;
        playPauseLabel.setText("⏸️");
        seekSlider.setValue(0);
        currentTimeLabel.setForeground(new Color(255, 255, 255));
        totalTimeLabel.setForeground(new Color(255, 255, 255));

        if (!isPrev || isShuffleEnabled) {
            prevList.add(song);
        }

        for (Song prevSong : prevList) {
            System.out.print(prevSong.getTitle() + "__");
        }
        System.out.println("\n-----------------");

        final List<Song> finalPlayList = playList;
        player.play(song.getId(), () -> {
            if (isRepeatEnabled) {
                playCurrentSong(); // Repeat
                return;
            }

            if (!queue.isEmpty()) {
                playCurrentSong(); // From queue
                return;
            }

            if (isShuffleEnabled) {
                currentIndex = (int) (Math.random() * songs.size());
                playCurrentSong();
                return;
            }

            // int nextIndex = currentIndex + 1;
            // if (nextIndex < songs.size()) {
            // currentIndex = nextIndex;
            isSearched = false;
            isPrev = false;
            isShuffleEnabled = true; // auto-enable shuffle
            shuffleLabel.setForeground(Color.GREEN); // reflect UI change
            playCurrentSong();
            // } else {
            //     currentIndex = 0;
            //     playCurrentSong(); // Loop
            // }

            stopSeekTimer();
        });

        startSeekTimer();
    }

    private void startSeekTimer() {
        stopSeekTimer();

        seekSlider.setEnabled(true);
        seekTimer = new Timer(500, (ActionEvent e) -> {
            if (!isSeeking) {
                Duration current = player.getCurrentTime();
                Duration total = player.getTotalDuration();
                if (!total.isUnknown() && total.toMillis() > 0) {
                    int value = (int) ((current.toMillis() / total.toMillis()) * 100);

                    // --- Prevent unnecessary change events ---
                    javax.swing.event.ChangeListener[] listeners = seekSlider.getChangeListeners();
                    for (javax.swing.event.ChangeListener listener : listeners) {
                        seekSlider.removeChangeListener(listener);
                    }

                    seekSlider.setValue(value);

                    for (javax.swing.event.ChangeListener listener : listeners) {
                        seekSlider.addChangeListener(listener);
                    }

                    currentTimeLabel.setText(formatTime(current));
                    totalTimeLabel.setText(formatTime(total));
                }
            }
        });

        seekTimer.start();
    }

    private void stopSeekTimer() {
        if (seekTimer != null) {
            seekTimer.stop();
            seekTimer = null;
        }
    }

    private String formatTime(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void filtersearchedSongs() {
        showSearchPanel();

        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            // 🔧 Reset flags and lists when search is cleared
            isSearched = false;
            searchedSongs.clear();
            // filteredsearchedSongs.clear();
            listModel.clear();

            // updateSongListDisplay(songs); // Optional: update JList with all songs
            hideSearchPanel();
            // centerLayout.show(centerpanel, "wheel"); // Show the wheel panel
            return;
        }

        // 🔎 Normal search logic
        isSearched = true;
        listModel.clear();
        // filteredsearchedSongs.clear();

        searchedSongs = service.getSearchedSongs(searchText); // Refresh the song list
        for (Song song : searchedSongs) {
            listModel.addElement(song);
            // filteredsearchedSongs.add(song);
        }

        if (listModel.size() > 0) {
            showSearchPanel();
        } else {
            Song nothingFound = new Song(0, "No results found", "", "");
            listModel.addElement(nothingFound);
            // filteredsearchedSongs.add(nothingFound);
        }
    }

    private void updateSongListDisplay(List<Song> songsToShow) {
        listModel.clear();
        for (Song song : songsToShow) {
            listModel.addElement(song);
        }
        songList.setModel(listModel);
    }

    private void showSearchPanel() {
        // searchResultsPanel.setBounds(searchField.getX(), searchField.getY() + searchField.getHeight(),
        // searchField.getWidth(), 120);
        // searchResultsPanel.setVisible(true);
        centerLayout.show(centerpanel, "search"); // Show the wheel panel
        searchResultsPanel.repaint();
    }

    private void hideSearchPanel() {
        centerLayout.show(centerpanel, "wheel"); // Show the wheel panel
    }

}

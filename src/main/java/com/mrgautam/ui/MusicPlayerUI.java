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
import java.awt.GridBagLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import javax.imageio.ImageIO;
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
    private boolean isFullscreen = false;

    private SongService service = new SongService();
    private CardLayout centerLayout;
    private JPanel centerpanel;
    private RotatingWheelPanel rotatingWheelPanel = new RotatingWheelPanel();
    private List<Song> prevList;

    private Boolean isSearched = false;
    private Boolean isPrev = false;

    private Boolean isLast;

    public MusicPlayerUI() {
        super();
        // Initialize JavaFX
        new javafx.embed.swing.JFXPanel();
        try {
            java.awt.Image icon = ImageIO.read(new File("src\\main\\resources\\icons\\converted_icon.png"));
            icon = icon.getScaledInstance(280, 220, java.awt.Image.SCALE_SMOOTH);
            // icon.setOpaque(true); // Removed as setOpaque is not applicable for Image
            setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setSize(1500, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false); // Remove default title bar
        setLayout(new BorderLayout());

        songs = service.getAllSongs();
        searchedSongs = service.getAllSongs();
        listModel = new DefaultListModel<>();
        for (Song song : searchedSongs) {
            listModel.addElement(song);
        }

        player = new AudioPlayer();

        prevList = new ArrayList<>();


        JPanel wheelWrapper = new JPanel(new GridBagLayout());
        wheelWrapper.setOpaque(true);
        wheelWrapper.setBackground(new Color(30, 30, 30));

        // Set fixed size for rotating wheel panel
        rotatingWheelPanel.setPreferredSize(new Dimension(1000, 500));
        rotatingWheelPanel.setMinimumSize(new Dimension(600, 300));

        wheelWrapper.add(rotatingWheelPanel);

        // Set minimum size for wrapper panel
        wheelWrapper.setMinimumSize(new Dimension(1000, 500));
        wheelWrapper.setPreferredSize(new Dimension(600, 300));

        centerLayout = new CardLayout();

        centerpanel = new JPanel(centerLayout);
        centerpanel.setMinimumSize(new Dimension(500, 500));
        centerpanel.add(wheelWrapper, "wheel");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save the previous song to database before closing

                try {
                    if (!prevList.isEmpty()) {

                        Song lastSong = prevList.get(prevList.size() - 1);
                        if (lastSong != null) {
                            service.addLastSong(lastSong); // Your DB save method
                            System.out.println("Saved as Last Song : " + lastSong.getTitle());
                        }
                    } else {
                        System.out.println("Closed Without Saving the last song");
                    }
                    // NotificationService nService = new NotificationService();
                    // nService.closeAppMail();

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    System.out.println("Closed Without Saving the last song");
                }

            }
        });

        // java.util.Timer  timer= new java.util.Timer();
        // // TimerTask for updating UI or handling periodic tasks (if needed)
        // TimerTask timerTask = new TimerTask() {
        //     @Override
        //     public void run() {
        //         try {
        //             nowPlayingLabel.setForeground(Color.WHITE);
        //             if (player.isPlaying()) {
        //                 nowPlayingLabel.setText("🎶 Now Playing: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
        //             } else {
        //                 nowPlayingLabel.setText("⏸️ Paused: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
        //             }
        //             timer.cancel();
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //     }
        // };
        rotatingWheelPanel.addImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileDialog fileDialog1 = new FileDialog((java.awt.Frame) null, "Select Image File", FileDialog.LOAD);
                fileDialog1.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
                fileDialog1.setVisible(true);
                if (fileDialog1.getFile() != null) {
                    try {
                        Song tempSong = service.getSongById(player.getMediaSongId());
                        tempSong.addSongImage(new File(fileDialog1.getDirectory(), fileDialog1.getFile()).getAbsolutePath());
                        try {
                            rotatingWheelPanel.setImage(null);
                            rotatingWheelPanel.setImage(rotatingWheelPanel.iconToBufferedImage(tempSong.getSongImage(tempSong.getId())));
                            nowPlayingLabel.setForeground(Color.orange);
                            nowPlayingLabel.setText("Image Updated for: " + service.getSongById(player.getMediaSongId()).getTitle());
                            startTimer(5000);
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                    } catch (Exception ex) {
                        System.err.println("Error adding song image: " + ex.getMessage());
                    }
                }
            }
        });
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
                label.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return label;
            }
        });

// -----------------------------------------------------------------------------------------------------------------------------
        // for add to queue popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addToQueueItem = new JMenuItem("➕ Add to Queue");
        JMenuItem editDetails = new JMenuItem("🛠️ Edit Details");
        JMenuItem deleteSong = new JMenuItem("❌ Delete Song");

        Font customFont = new Font("Segoe UI Symbol", Font.BOLD, 14);
        Color bgColor = new Color(40, 40, 40);   // Dark background
        Color fgColor = Color.WHITE;            // Light text

        for (JMenuItem item : new JMenuItem[]{addToQueueItem, editDetails, deleteSong}) {
            item.setFont(customFont);
            item.setForeground(fgColor);
            item.setBackground(bgColor);
            item.setOpaque(true);
            item.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        }

// Style the popup itself
        popupMenu.setBackground(bgColor);
        popupMenu.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        popupMenu.add(addToQueueItem);
        popupMenu.add(editDetails);
        popupMenu.add(deleteSong);
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

        int lastSongIndex = service.getLastSongIndex();
        if (lastSongIndex != -1) {
            isLast = true;
            playCurrentSong();
            player.pause();
            rotatingWheelPanel.stopRotation();
            playPauseLabel.setText("▶️");
            playPauseLabel.setForeground(Color.WHITE);
            nowPlayingLabel.setText("⏸️ Paused: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
        } else {
            isLast = false;
        }

        // --------------------- Play/Pause Button ---------------------
        playPauseLabel.setPreferredSize(new Dimension(100, 80)); // Set preferred size for the labe
        playPauseLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 40)); // try different fonts
        playPauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        playPauseLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, -10)); // Set border for the label

        playPauseLabel.setForeground(Color.WHITE); // Default color
        playPauseLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        playPauseLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (player.isPlaying()) {
                    player.pause();
                    nowPlayingLabel.setForeground(Color.white);
                    rotatingWheelPanel.stopRotation();

                    playPauseLabel.setText("▶️");
                    playPauseLabel.setForeground(Color.WHITE); // Reset color
                    nowPlayingLabel.setText("⏸️ Paused: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                } else {
                    if (player.isPaused()) {
                        rotatingWheelPanel.startRotation();

                        player.resume();
                        nowPlayingLabel.setForeground(Color.white);
                        playPauseLabel.setForeground(Color.GREEN); // Reset color
                        playPauseLabel.setText("⏸️");

                        nowPlayingLabel.setText("🎶 Now Playing: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playPauseLabel.setForeground(Color.GREEN); // Hover effect
                playPauseLabel.setFont(playPauseLabel.getFont().deriveFont(Font.BOLD));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!player.isPlaying()) {
                    playPauseLabel.setForeground(Color.WHITE); // Reset color
                    playPauseLabel.setFont(playPauseLabel.getFont().deriveFont(Font.PLAIN));

                } // else keep it green
                // else keep it green
            }
        });
        // --------------------- Play/Pause Button ends here ---------------------

        //------------------------
        // Next Button
        nextLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        nextLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 25)); // try different fonts
        nextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nextLabel.setVerticalAlignment(SwingConstants.CENTER);
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

                playCurrentSong();
                playPauseLabel.setText("⏸️");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                nextLabel.setForeground(Color.GREEN); // Hover effect
                nextLabel.setFont(nextLabel.getFont().deriveFont(Font.BOLD));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                nextLabel.setForeground(Color.WHITE);
                nextLabel.setFont(nextLabel.getFont().deriveFont(Font.PLAIN));
            }
        });

        //    /-------------------next button ends here------------------
        // --------------------- Previous Button ---------------------
        prevLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        prevLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 25)); // try different fonts
        prevLabel.setHorizontalAlignment(SwingConstants.CENTER);
        prevLabel.setVerticalAlignment(SwingConstants.CENTER);
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
                prevList = getPrevList();
                if (prevList.size() == 0) {
                    isPrev = false;

                    player.stop();
                    rotatingWheelPanel.stopRotation();
                    rotatingWheelPanel.setVisible(false);
                    nowPlayingLabel.setVisible(true);
                    nowPlayingLabel.setText("⏸️ No previous song found.");
                    nowPlayingLabel.setForeground(Color.RED);
                    playPauseLabel.setText("▶️");
                    prevLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    prevLabel.setVerticalAlignment(SwingConstants.CENTER);
                    playPauseLabel.setForeground(Color.WHITE);
                } else {
                    currentIndex = prevList.size() - 1;
                    playCurrentSong();
                    playPauseLabel.setText("⏸️");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                prevLabel.setForeground(Color.GREEN); // Hover effect
                prevLabel.setFont(prevLabel.getFont().deriveFont(Font.BOLD));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                prevLabel.setForeground(Color.WHITE);
                prevLabel.setFont(prevLabel.getFont().deriveFont(Font.PLAIN));
            }
        });

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
            public void mouseClicked(MouseEvent e) {
                double percent = seekSlider.getValue() / 100.0;
                player.seekTo(percent); // Seek only after releasing
            }

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
        shuffleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        shuffleLabel.setVerticalAlignment(SwingConstants.CENTER);
        shuffleLabel.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 10));
        shuffleLabel.setForeground(Color.WHITE); // Default color
        shuffleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        shuffleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isShuffleEnabled = !isShuffleEnabled;
                if (isShuffleEnabled) {
                    shuffleLabel.setForeground(Color.GREEN);
                    isRepeatEnabled = false;
                    repeatLabel.setForeground(Color.WHITE);
                    isPrev = false;
                } else {
                    shuffleLabel.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                shuffleLabel.setForeground(Color.GREEN);
                shuffleLabel.setFont(shuffleLabel.getFont().deriveFont(Font.BOLD));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isShuffleEnabled) {
                    shuffleLabel.setForeground(Color.WHITE);
                }
                shuffleLabel.setFont(shuffleLabel.getFont().deriveFont(Font.PLAIN));

            }
        });

        // repeat label
        repeatLabel.setPreferredSize(new Dimension(50, 50)); // Set preferred size for the labe
        repeatLabel.setFont(new Font("Seoge UI Symbol", Font.BOLD, 15)); // try different fonts
        repeatLabel.setHorizontalAlignment(SwingConstants.CENTER);
        repeatLabel.setVerticalAlignment(SwingConstants.CENTER);
        repeatLabel.setBorder(BorderFactory.createEmptyBorder(7, -5, 0, 0));
        repeatLabel.setForeground(Color.WHITE); // Default color
        repeatLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        repeatLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isRepeatEnabled = !isRepeatEnabled;
                if (isRepeatEnabled) {
                    repeatLabel.setForeground(Color.GREEN);
                    Song currentSong = service.getSongById(player.getMediaSongId());
                    if (currentSong != null) {
                        prevList.clear();
                        prevList.add(currentSong);
                    }
                    isPrev = false;
                    isShuffleEnabled = false;
                    shuffleLabel.setForeground(Color.WHITE);
                } else {
                    repeatLabel.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repeatLabel.setForeground(Color.GREEN);
                repeatLabel.setFont(repeatLabel.getFont().deriveFont(Font.BOLD));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isRepeatEnabled) {
                    repeatLabel.setForeground(Color.WHITE);
                }
                repeatLabel.setFont(repeatLabel.getFont().deriveFont(Font.PLAIN));
            }
        });

        addToQueueItem.addActionListener(e -> {

            Song selectedSong = songList.getSelectedValue(); // safer and cleaner

            if (selectedSong != null && !selectedSong.getTitle().equals("No results found")) {
                queue.add(selectedSong);
                nowPlayingLabel.setText("✅ Added to Queue: " + selectedSong.getTitle() + " - " + selectedSong.getArtist());
                nowPlayingLabel.setForeground(Color.GREEN);
                startTimer(5000);

            }
        });

        editDetails.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Boolean isUpdated = false;
                Song selectedSong = songList.getSelectedValue(); // safer and cleaner

                if (selectedSong != null) {
                    EditSongDetailsPanel editDetailsPanel = new EditSongDetailsPanel(selectedSong);

                    if (editDetailsPanel.inputResult == JOptionPane.OK_OPTION) {
                        String newTitle = editDetailsPanel.titleField.getText().trim();
                        String newArtist = editDetailsPanel.artistField.getText().trim();
                        String newImagePath = editDetailsPanel.songImageFeild.getText().trim();

                        if (!(newTitle.equals(selectedSong.getTitle()) || newTitle.equals(""))) {
                            selectedSong.updateTitle(newTitle);
                            isUpdated = true;
                        }
                        if (!(newArtist.equals(selectedSong.getArtist()) || newArtist.equals(""))) {
                            selectedSong.updateArtist(newArtist);
                            isUpdated = true;
                        }
                        if (!(newImagePath.equals(""))) {
                            selectedSong.updateSongImage(newImagePath);
                            rotatingWheelPanel.setImage(rotatingWheelPanel.iconToBufferedImage(selectedSong.getSongImage(selectedSong.getId())));
                            isUpdated = true;

                        }

                        if (isUpdated) {
                            songs = service.getAllSongs();
                            updateSongListDisplay(songs);
                            filtersearchedSongs();
                            nowPlayingLabel.setForeground(Color.orange);
                            nowPlayingLabel.setText("Details Updated for: " + selectedSong.getTitle());
                            startTimer(5000);
                        }

                    }
                }
            }

            public void maybeShowPopup(MouseEvent e) {
                if (!"No results found".equalsIgnoreCase(songList.getSelectedValue().getTitle())) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    System.out.println("Popup blocked for restricted content.");
                }
            }

        });

        deleteSong.addActionListener(e -> {

            Song selectedSong = songList.getSelectedValue(); // safer and cleaner

            boolean confirm = DeleteConfirmationPopup.showDeleteConfirmation(null, selectedSong.getTitle());

            if (confirm) {
                // Proceed with delete logic
                System.out.println("Deleting song...");
                Boolean isDeleted = service.deleteSong(selectedSong);
                if (isDeleted) {
                    nowPlayingLabel.setForeground(Color.RED);
                    nowPlayingLabel.setText(selectedSong.getTitle() + " has been deleted");
                    songs = service.getAllSongs();
                    updateSongListDisplay(songs);
                    filtersearchedSongs();
                    startTimer(5000);

                } else {
                    nowPlayingLabel.setForeground(Color.RED);
                    nowPlayingLabel.setText("Delete canceled");
                    startTimer(5000);

                }
            }

            // }
        });

        // ----------------------------------------------------------------------
        //To make the right-click work more naturally (when user right-clicks but doesn't select first), add this:
        songList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                isSearched = true;
                if (e.getClickCount() == 2) {
                    isRepeatEnabled = false; // disable repeat
                    repeatLabel.setForeground(Color.WHITE);
                    isShuffleEnabled = false; // disable shuffle
                    isPrev = false; // disable previous
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

            private void maybeShowPopup(MouseEvent e) {
                if (!"No results found".equalsIgnoreCase(songList.getSelectedValue().getTitle())) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    System.out.println("Popup blocked for restricted content.");
                }
            }
        }
        );

        // addsong label ------------------------------------------------------
        addSongLabel.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e
            ) {
                // Step 1: Choose File (MP3)
                FileDialog fileDialog = new FileDialog((java.awt.Frame) null, "Select MP3 File", FileDialog.LOAD);
                fileDialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                fileDialog.setVisible(true);

                String selectedFileName = fileDialog.getFile();
                String selectedDir = fileDialog.getDirectory();

                if (selectedFileName != null) {
                    File selectedFile = new File(selectedDir, selectedFileName);

                    if (selectedFile != null && selectedFile.getName().toLowerCase().endsWith(".mp3")) {

                        AddSongDetailsPanel addSongDetailsPanel = new AddSongDetailsPanel();

                        // addSongDetailsPanel.addImageButton.addActionListener(ev -> {
                        //     FileDialog fileDialog1 = new FileDialog((java.awt.Frame) null, "Select Image File", FileDialog.LOAD);
                        //     fileDialog1.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg"));
                        //     fileDialog1.setVisible(true);
                        //     if (fileDialog1.getFile() != null) {
                        //         addSongDetailsPanel.songImageFeild.setText(new File(fileDialog1.getDirectory(), fileDialog1.getFile()).getAbsolutePath());
                        //     }
                        // });
                        if (addSongDetailsPanel.inputResult == JOptionPane.OK_OPTION) {
                            String title = addSongDetailsPanel.titleField.getText().trim();
                            String artist = addSongDetailsPanel.artistField.getText().trim();
                            String imagePath = addSongDetailsPanel.songImageFeild.getText().trim();

                            if (!title.isEmpty() && !artist.isEmpty()) {
                                Song newSong = new Song(0, title, artist, selectedFile.getAbsolutePath());
                                boolean isSongAdded = service.addSong(newSong);

                                try {
                                    if (!imagePath.isEmpty()) {
                                        newSong.setId(service.getLatestSongId());
                                        newSong.addSongImage(imagePath);
                                    }

                                } catch (Exception ex) {
                                    System.out.println("Error adding song image: " + ex.getMessage());
                                }

                                if (isSongAdded) {
                                    searchedSongs = service.getAllSongs();
                                    listModel.clear();
                                    updateSongListDisplay(searchedSongs);
                                    filtersearchedSongs(); // refresh if searched
                                    nowPlayingLabel.setText("🎉 Added: " + title + " - " + artist);
                                    nowPlayingLabel.setForeground(Color.GREEN);
                                    songList.setModel(listModel);
                                    startTimer(5000);

                                } else {
                                    nowPlayingLabel.setText("❌ Something went wrong with adding the song");
                                    nowPlayingLabel.setForeground(Color.RED);
                                    startTimer(5000);

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
            public void mouseEntered(MouseEvent e
            ) {
                addSongLabel.setForeground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e
            ) {
                addSongLabel.setForeground(Color.LIGHT_GRAY);

            }
        }
        );
        downloadSongLabel.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e
            ) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("java", "OpenWebsite.java");
                    // pb.inheritIO(); // To show the output from OtherProgram
                    Process process = pb.start();
                    process.waitFor(); // Wait for the other program to finish
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                // Implement your download logic here
                nowPlayingLabel.setText("After Download the Song , add using '➕ Add Song'");
                nowPlayingLabel.setForeground(Color.orange);
                startTimer(10000);

            }

            @Override
            public void mouseEntered(MouseEvent e
            ) {
                downloadSongLabel.setForeground(Color.green);
            }

            @Override
            public void mouseExited(MouseEvent e
            ) {
                downloadSongLabel.setForeground(Color.LIGHT_GRAY);
            }
        }
        );

        // Add tooltips to controls
        playPauseLabel.setToolTipText(
                "Play/Pause (Space)");
        nextLabel.setToolTipText(
                "Next Song (→)");
        prevLabel.setToolTipText(
                "Previous Song (←)");
        shuffleLabel.setToolTipText(
                "Shuffle (S)");
        repeatLabel.setToolTipText(
                "Repeat (R)");
        addSongLabel.setToolTipText(
                "Add Local MP3 File");
        downloadSongLabel.setToolTipText(
                "Download from YouTube");
        volumeButton.setToolTipText(
                "Volume Control");

        // Improve seek slider appearance
        seekSlider.setUI(
                new RoundedSliderUI(seekSlider));
        // seekSlider.setBackground(new Color(30, 30, 30));
        seekSlider.setBackground(Color.BLACK);

        seekSlider.setForeground(Color.GREEN);

        seekSlider.setPreferredSize(
                new Dimension(400, 20));
        seekSlider.setFocusable(
                false);

        // Improve time labels
        currentTimeLabel.setForeground(Color.WHITE);

        totalTimeLabel.setForeground(Color.WHITE);

        currentTimeLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        totalTimeLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));

        searchPanel.setBackground(
                new Color(0, 0, 0, (int) (1.0f * 255)));
        searchPanel.add(searchField);

        // Slider Panel
        JPanel sliderPanel = new JPanel();

        sliderPanel.setLayout(
                new FlowLayout(FlowLayout.CENTER));
        sliderPanel.setOpaque(
                false);
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 10));
        sliderPanel.add(currentTimeLabel, BorderLayout.WEST);

        sliderPanel.add(seekSlider);

        sliderPanel.add(totalTimeLabel, BorderLayout.EAST);

        // Controls Panel
        JPanel buttonsPanel = new JPanel();

        buttonsPanel.setLayout(
                new FlowLayout());
        buttonsPanel.setBackground(Color.BLACK);

        buttonsPanel.add(shuffleLabel);

        buttonsPanel.add(prevLabel);

        buttonsPanel.add(playPauseLabel);

        buttonsPanel.add(nextLabel);

        buttonsPanel.add(repeatLabel);

        // Bottom Panel Layout
        bottomPanel.setLayout(
                new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.BLACK);

        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(nowPlayingLabel);

        nowPlayingLabel.setAlignmentX(bottomPanel.CENTER_ALIGNMENT);

        bottomPanel.add(buttonsPanel);

        buttonsPanel.setAlignmentX(bottomPanel.CENTER_ALIGNMENT);

        bottomPanel.add(sliderPanel, CENTER_ALIGNMENT);

        bottomPanel.setAlignmentX(bottomPanel.CENTER_ALIGNMENT);

        bottomPanel.add(Box.createVerticalStrut(20));

        // Search Results Panel
        searchResultsPanel = new JPanel(new BorderLayout());

        searchResultsPanel.setVisible(
                false);
        searchResultsPanel.setBackground(
                new Color(30, 30, 30));
        searchResultsPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.setVerticalScrollBar(SpotifyScrollBarUI.getScrollbar());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);  // higher = faster
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        searchResultsPanel.add(scrollPane, BorderLayout.CENTER);

        searchResultsPanel.setPreferredSize(new Dimension(1500, 700));
        searchResultsPanel.setBounds(searchField.getX(), searchField.getY() + searchField.getHeight(), searchField.getWidth(), 120);
        centerpanel.add(searchResultsPanel,
                "search");

        // Add Song Panel
        addSongLabel.setForeground(Color.LIGHT_GRAY);

        addSongLabel.setFont(
                new Font("Segoe UI Symbol", Font.PLAIN, 14));
        addSongLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        downloadSongLabel.setForeground(Color.LIGHT_GRAY);

        downloadSongLabel.setFont(
                new Font("Segoe UI Symbol", Font.PLAIN, 14));
        downloadSongLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        downloadSongLabel.setOpaque(
                true);
        downloadSongLabel.setBackground(
                new Color(50, 50, 50));
        downloadSongLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel addSongMethodsContainer = new JPanel(new BorderLayout());

        addSongMethodsContainer.setOpaque(
                true);
        addSongMethodsContainer.setBackground(
                new Color(50, 50, 50));
        addSongMethodsContainer.add(addSongLabel, BorderLayout.WEST);

        addSongMethodsContainer.add(downloadSongLabel, BorderLayout.EAST);

        searchResultsPanel.add(addSongMethodsContainer, BorderLayout.SOUTH);

        addSongLabel.setOpaque(
                true);
        addSongLabel.setBackground(
                new Color(50, 50, 50));
        addSongLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // JPanel topPanel = new JPanel(new BorderLayout());
        // topPanel.add(titlePanel,BorderLayout.NORTH);
        // topPanel.add(searchPanel,BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);

        add(centerpanel);

        add(bottomPanel, BorderLayout.SOUTH);

        MouseAdapter globalFocusListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Component clicked = SwingUtilities.getDeepestComponentAt(e.getComponent(), e.getX(), e.getY());
                if (clicked != searchField && clicked != songList) {
                    isSearched = false; // Reset search state
                    hideSearchPanel();
                    requestFocusInWindow();    // Remove focus from search
                }
            }
        };

        // Attach to all background components (mainPanel, bottomPanel, etc.)
        bottomPanel.addMouseListener(globalFocusListener);

        searchPanel.addMouseListener(globalFocusListener);

        buttonsPanel.addMouseListener(globalFocusListener);

        // Add keyboard shortcuts
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_SPACE -> {
                                if (player.isPlaying()) {
                                    player.pause();
                                    playPauseLabel.setText("▶️");
                                    rotatingWheelPanel.stopRotation();
                                    return true;

                                } else {
                                    if (player.isPaused()) {
                                        player.resume();
                                        playPauseLabel.setText("⏸️");
                                        rotatingWheelPanel.startRotation();
                                    } else if (currentIndex != -1) {
                                        playCurrentSong();
                                    }
                                    return true;
                                }
                            }

                            case KeyEvent.VK_S -> {
                                isShuffleEnabled = !isShuffleEnabled;
                                if (isShuffleEnabled) {
                                    shuffleLabel.setForeground(Color.GREEN);
                                    isRepeatEnabled = false;
                                    repeatLabel.setForeground(Color.WHITE);
                                    isPrev = false;
                                    return true;

                                } else {
                                    shuffleLabel.setForeground(Color.WHITE);
                                    return true;

                                }
                            }
                            case KeyEvent.VK_R -> {
                                isRepeatEnabled = !isRepeatEnabled;
                                if (isRepeatEnabled) {
                                    repeatLabel.setForeground(Color.GREEN);
                                    Song currentSong = service.getSongById(player.getMediaSongId());
                                    if (currentSong != null) {
                                        prevList.clear();
                                        prevList.add(currentSong);
                                    }
                                    isPrev = false;
                                    isShuffleEnabled = false;
                                    shuffleLabel.setForeground(Color.WHITE);
                                    return true;

                                } else {
                                    repeatLabel.setForeground(Color.WHITE);
                                    return true;

                                }
                            }
                            case KeyEvent.VK_DOWN -> {
                                if(player.getVolume() != 0.0){
                                    player.setVolume(player.getVolume() - 0.1);
                                }
                            }
                            case KeyEvent.VK_UP -> {
                                if(player.getVolume() != 0.0){
                                    player.setVolume(player.getVolume() + 0.1);
                                }
                            }
                            
                        }
                    }
                    return false;
                });

        // Make sure the window can receive key events
        setFocusable(true);
        requestFocusInWindow();
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE -> {
                if (player.isPlaying()) {
                    player.pause();
                    playPauseLabel.setText("▶️");
                    rotatingWheelPanel.stopRotation();
                } else {
                    if (player.isPaused()) {
                        player.resume();
                        playPauseLabel.setText("⏸️");
                        rotatingWheelPanel.startRotation();
                    } else if (currentIndex != -1) {
                        playCurrentSong();
                    }
                }
            }

            case KeyEvent.VK_S -> {
                isShuffleEnabled = !isShuffleEnabled;
                if (isShuffleEnabled) {
                    shuffleLabel.setForeground(Color.GREEN);
                    isRepeatEnabled = false;
                    repeatLabel.setForeground(Color.WHITE);
                    isPrev = false;
                } else {
                    shuffleLabel.setForeground(Color.WHITE);
                }
            }
            case KeyEvent.VK_R -> {
                isRepeatEnabled = !isRepeatEnabled;
                if (isRepeatEnabled) {
                    repeatLabel.setForeground(Color.GREEN);
                    Song currentSong = service.getSongById(player.getMediaSongId());
                    if (currentSong != null) {
                        prevList.clear();
                        prevList.add(currentSong);
                    }
                    isPrev = false;
                    isShuffleEnabled = false;
                    shuffleLabel.setForeground(Color.WHITE);
                } else {
                    repeatLabel.setForeground(Color.WHITE);
                }
            }

        }
    }

    // -------------------------------------------METHODS------------------------------------------------------
    private void playCurrentSong() {
        seekSlider.setValue(0);
        nowPlayingLabel.setForeground(Color.white);
        // Stop the current song before playing the next one
        player.stop();
        stopSeekTimer();
        // centerLayout.show(centerpanel, "wheel");

        playPauseLabel.setForeground(Color.GREEN); // Reset color
        hideSearchPanel();
        List<Song> playList = service.getAllSongs(); // Default to all songs

        // Decide playlist based on search & shuffle state
        if (isSearched) {
            playList = searchedSongs;
        }
        if (isPrev && !isSearched) {
            playList = prevList;
        }

        // If shuffle is enabled, pick random index from 'songs'
        if (isShuffleEnabled) {
            currentIndex = (int) (Math.random() * songs.size());
            playList = songs; // Always shuffle from full song list
        }

        // Handle empty playlist safely
        if (playList.isEmpty() || currentIndex < 0 || currentIndex >= playList.size()) {

            // if(isPrev){
            if (isPrev) {
                playList = prevList;

            } else {
                playList = songs;
                currentIndex = (int) (Math.random() * songs.size()); // Random index from full list
            }

        }
        Song song = playList.get(currentIndex);

        // Priority to queue
        if (!queue.isEmpty() && !isRepeatEnabled && !isPrev) {
            song = queue.remove(0);
            currentIndex = playList.indexOf(song); // Use full list for indexing
        } else if (isPrev) {
            song = prevList.remove(prevList.size() - 1); // Remove last played song
            currentIndex = prevList.indexOf(song); // Use full list for indexing
        } else if (isLast) {
            song = service.getSongById(service.getLastSongIndex());
            isLast = false;
        } else {
            song = playList.get(currentIndex);
        }

        if (isRepeatEnabled) {
            song = service.getSongById(player.getMediaSongId()); // Use full list for indexing
        }

        nowPlayingLabel.setText("🎶 Now Playing: " + song.getTitle() + " - " + song.getArtist());
        songList.setSelectedIndex(currentIndex);
        isPlaying[0] = true;
        playPauseLabel.setText("⏸️");
        currentTimeLabel.setForeground(new Color(255, 255, 255));
        totalTimeLabel.setForeground(new Color(255, 255, 255));

        if (!isPrev || isShuffleEnabled) {
            if (!isRepeatEnabled) {
                updatePrevList(song);

            }
        }
        System.out.println("\nPrevSongs:");
        for (Song prevSong : prevList) {
            System.out.print(prevList.indexOf(prevSong) + ". " + prevSong.getTitle() + " | ");
        }

        rotatingWheelPanel.stopRotation();
        rotatingWheelPanel.setVisible(false);
        try {
            rotatingWheelPanel.setImage(null);
            rotatingWheelPanel.setImage(rotatingWheelPanel.iconToBufferedImage(song.getSongImage(song.getId())));
        } catch (Exception e) {
            e.getMessage();
        }
        rotatingWheelPanel.setVisible(true);
        rotatingWheelPanel.startRotationAtNewSong();

        // final List<Song> finalPlayList = playList;
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

            isSearched = false;
            isPrev = false;
            if (!isRepeatEnabled) {
                isShuffleEnabled = true; // auto-enable shuffle
            }
            shuffleLabel.setForeground(Color.GREEN); // reflect UI change
            playCurrentSong();
        });

        startSeekTimer();
    }

    private void startSeekTimer() {
        stopSeekTimer();
        seekSlider.setEnabled(true);
        seekTimer = new Timer(500, (ActionEvent e) -> {
            int value;
            if (!isSeeking) {
                Duration current = player.getCurrentTime();
                Duration total = player.getTotalDuration();
                if (!total.isUnknown() && total.toMillis() > 0) {
                    value = (int) ((current.toMillis() / total.toMillis()) * 100);

                    // --- Prevent unnecessary change events ---
                    javax.swing.event.ChangeListener[] listeners = seekSlider.getChangeListeners();
                    for (javax.swing.event.ChangeListener listener : listeners) {
                        seekSlider.removeChangeListener(listener);
                    }

                    seekSlider.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int clickedValue = (int) ((e.getX() / (double) seekSlider.getWidth()) * 100);
                            seekSlider.setValue(clickedValue);
                        }
                    });
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
        searchedSongs = service.getSearchedSongs(searchText); // Refresh the song list
        updateSongListDisplay(searchedSongs);

        if (searchText.equals("@all") || searchText.equals("@ all")) {
            searchedSongs = service.getAllSongs(); // Refresh the song list
            updateSongListDisplay(searchedSongs);
        } else if (searchText.equals("@previous") || searchText.equals("@ previous")) {
            updateSongListDisplay(prevList);
        } else if (searchText.equalsIgnoreCase("@queue") || searchText.equalsIgnoreCase("@queue")) {
            updateSongListDisplay(queue);
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

        centerLayout.show(centerpanel, "search"); // Show the wheel panel
        searchResultsPanel.repaint();
    }

    private void hideSearchPanel() {
        centerLayout.show(centerpanel, "wheel"); // Show the wheel panel
    }

    private void updatePrevList(Song song) {

        prevList.add(song);

    }

    private List<Song> getPrevList() {
        return prevList;
    }

    // // Add these new methods for keyboard shortcuts
    // private void toggleFullscreen() {
    //     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    //     GraphicsDevice gd = ge.getDefaultScreenDevice();
    //     if (!isFullscreen) {
    //         dispose();
    //         setUndecorated(true);
    //         gd.setFullScreenWindow(this);
    //         isFullscreen = true;
    //     } else {
    //         gd.setFullScreenWindow(null);
    //         setUndecorated(false);
    //         setVisible(true);
    //         isFullscreen = false;
    //     }
    // }
    void startTimer(int delay) {

        java.util.Timer timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    nowPlayingLabel.setForeground(Color.WHITE);
                    if (player.isPlaying()) {
                        nowPlayingLabel.setText("🎶 Now Playing: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                    } else {
                        nowPlayingLabel.setText("⏸️ Paused: " + service.getSongById(player.getMediaSongId()).getTitle() + " - " + service.getSongById(player.getMediaSongId()).getArtist());
                    }

                    timer.cancel(); // cancel after this execution

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        timer.schedule(timerTask, delay); // Run once immediately (0 ms delay)
    }

}

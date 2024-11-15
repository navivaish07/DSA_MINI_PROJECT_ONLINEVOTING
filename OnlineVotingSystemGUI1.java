import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Class to represent a candidate
class Candidate {
    String name;
    int votes;

    public Candidate(String name) {
        this.name = name;
        this.votes = 0;
    }

    // Increment the vote count
    public void addVote() {
        this.votes++;
    }

    // Add multiple votes
    public void addVotes(int numVotes) {
        this.votes += numVotes;
    }

    // Get current votes
    public int getVotes() {
        return this.votes;
    }

    @Override
    public String toString() {
        return name + " (" + votes + " votes)";
    }
}

// Main voting system class with GUI
public class OnlineVotingSystemGUI1 {
    private static Queue<String> voterQueue = new LinkedList<>();
    private static Stack<String> votingHistory = new Stack<>();
    private static Candidate[] candidates;
    private static int maxVotersPerCycle = 10; // Maximum number of voters per cycle
    private static int votersThisCycle = 0; // Counter for voters in the current cycle
    private static int totalVoters;  // Total voters in the simulation (user-defined)
    private static int totalVotersVoted = 0; // Track total number of voters voted

    private static JFrame frame;
    private static JTextArea votingHistoryArea;
    private static JLabel voteStatusLabel;
    private static JLabel voteCountsLabel;
    private static JButton voteButton1;
    private static JButton voteButton2;
    private static JButton voteButton3;
    private static JButton nextVoterButton;
    private static JButton showResultsButton;
    private static String currentVoter;

    public static void main(String[] args) {
        // Ask the user for the number of voters
        String input = JOptionPane.showInputDialog("Enter the number of voters:");
        try {
            totalVoters = Integer.parseInt(input);
            if (totalVoters <= 0) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid number of voters.");
                return; // Exit if the user input is invalid
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.");
            return; // Exit if the user input is invalid
        }

        // Initialize candidates
        candidates = new Candidate[] {
            new Candidate("Alice"),
            new Candidate("Bob"),
            new Candidate("Charlie")
        };

        // Add voters to the queue (Simulating in real-world use case)
        for (int i = 1; i <= totalVoters; i++) {  // Use the user-defined number of voters
            voterQueue.offer("Voter " + i);
        }

        // Set up the JFrame for the GUI
        frame = new JFrame("Online Voting System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Voting History TextArea
        votingHistoryArea = new JTextArea(10, 40);
        votingHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(votingHistoryArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Vote Counts Display (Only visible when voting is over)
        voteCountsLabel = new JLabel("Current Vote Counts: ");
        frame.add(voteCountsLabel, BorderLayout.NORTH);

        // Status label for displaying which voter is voting
        voteStatusLabel = new JLabel("No voters in queue.");
        frame.add(voteStatusLabel, BorderLayout.SOUTH);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        voteButton1 = new JButton("Vote for Alice");
        voteButton2 = new JButton("Vote for Bob");
        voteButton3 = new JButton("Vote for Charlie");

        buttonPanel.add(voteButton1);
        buttonPanel.add(voteButton2);
        buttonPanel.add(voteButton3);

        frame.add(buttonPanel, BorderLayout.EAST);

        // Next Voter button
        nextVoterButton = new JButton("Next Voter");
        nextVoterButton.setEnabled(false);  // Initially disabled
        buttonPanel.add(nextVoterButton);

        // Show Results button
        showResultsButton = new JButton("Show Final Results");
        showResultsButton.setEnabled(false);  // Initially disabled
        frame.add(showResultsButton, BorderLayout.WEST);

        frame.setVisible(true);

        // Set up action listeners for vote buttons
        voteButton1.addActionListener(e -> castVote(0));
        voteButton2.addActionListener(e -> castVote(1));
        voteButton3.addActionListener(e -> castVote(2));
        nextVoterButton.addActionListener(e -> nextVoter());
        showResultsButton.addActionListener(e -> showResults());

        // Start with the first voter
        nextVoter();
    }

    // Method to cast a vote for the selected candidate
    private static void castVote(int candidateIndex) {
        if (currentVoter != null) {
            // Cast the vote
            candidates[candidateIndex].addVote();
            votingHistory.push(currentVoter + " voted for " + candidates[candidateIndex].name);
            updateVoteCounts();

            // Disable vote buttons after voting
            enableVoteButtons(false);

            // Increment the voter counter for the current cycle
            votersThisCycle++;
            totalVotersVoted++;

            // Check if we have reached the max voters for this cycle
            if (votersThisCycle < maxVotersPerCycle) {
                // If not yet, move to the next voter automatically
                nextVoter();
            } else {
                // If the cycle is complete, enable the "Next Voter" button
                nextVoterButton.setEnabled(true);
            }
        }
    }

    // Move to the next voter
    private static void nextVoter() {
        if (!voterQueue.isEmpty() && votersThisCycle < maxVotersPerCycle) {
            // Get the next voter from the queue
            currentVoter = voterQueue.poll(); // Remove the voter from the queue
            voteStatusLabel.setText(currentVoter + " is voting...");
            enableVoteButtons(true);  // Enable vote buttons for the current voter
            nextVoterButton.setEnabled(false);  // Disable "Next Voter" button until voting is complete
        } else if (votersThisCycle >= maxVotersPerCycle) {
            // If the cycle has ended (10 voters have voted), start a new cycle
            endCycle();
        }

        // Check if all voters have voted and display the results
        if (totalVotersVoted >= totalVoters) {
            showResultsButton.setEnabled(true); // Enable "Show Results" button once all voters have voted
        }
    }

    // Enable or disable vote buttons
    private static void enableVoteButtons(boolean enable) {
        voteButton1.setEnabled(enable);
        voteButton2.setEnabled(enable);
        voteButton3.setEnabled(enable);
    }

    // Update the vote counts display
    private static void updateVoteCounts() {
        StringBuilder voteCounts = new StringBuilder("<html><b>Current Vote Counts:</b><br>");
        for (Candidate candidate : candidates) {
            voteCounts.append(candidate.toString()).append("<br>");
        }
        voteCounts.append("</html>");
        voteCountsLabel.setText(voteCounts.toString());
        votingHistoryArea.setText(getVotingHistory());
    }

    // Get the full voting history
    private static String getVotingHistory() {
        StringBuilder history = new StringBuilder();
        for (String vote : votingHistory) {
            history.append(vote).append("\n");
        }
        return history.toString();
    }

    // End the current cycle (10 voters have voted)
    private static void endCycle() {
        voteStatusLabel.setText("Cycle ended: " + maxVotersPerCycle + " voters have voted.");
        // Reset cycle counters for the next batch
        votersThisCycle = 0;

        // Enable "Next Voter" button for the next cycle
        if (!voterQueue.isEmpty()) {
            nextVoterButton.setEnabled(true);
        }
    }

    // Show the final voting results when the button is clicked
    private static void showResults() {
        // Find the winner (candidate with the most votes)
        Candidate winner = candidates[0];
        for (Candidate candidate : candidates) {
            if (candidate.getVotes() > winner.getVotes()) {
                winner = candidate;
            }
        }

        StringBuilder results = new StringBuilder("Final Voting Results:\n");
        for (Candidate candidate : candidates) {
            results.append(candidate).append("\n");
        }

        // Display who won the voting
        results.append("\nThe winner is: ").append(winner.name).append(" with ").append(winner.getVotes()).append(" votes!");

        JOptionPane.showMessageDialog(frame, results.toString(), "Voting Results", JOptionPane.INFORMATION_MESSAGE);
    }
}
